package net.kotek.jdbm;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * StorageDirect which provides transactions.
 * Index file data are stored in memory+trans log, phys file data are stored only in transaction log.
 *
 */
public class StorageTrans extends Storage implements RecordManager{

    protected static final long WRITE_INDEX_LONG = 1L <<48;
    protected static final long WRITE_INDEX_LONG_ZERO = 2L <<48;
    protected static final long WRITE_PHYS_LONG = 3L <<48;
    protected static final long WRITE_PHYS_ARRAY = 4L <<48;

    protected static final long WRITE_SKIP_BUFFER = 444L <<48;
    /** last instruction in log file */
    protected static final long WRITE_SEAL = 111L <<48;
    /** added to offset 8 into log file, indicates that it was sucesfully written*/
    protected static final long LOG_SEAL = 4566556446554645L;
    public static final String TRANS_LOG_FILE_EXT = ".t";


    protected ByteBuffer2 transLog;
    protected long transLogOffset;


    protected long indexSize;
    protected long physSize;
    protected final LongMap<Long> recordLogRefs = new LongConcurrentHashMap<Long>();
    protected final LongMap<Long> recordIndexVals = new LongConcurrentHashMap<Long>();
    protected final LongMap<long[]> longStackPages = new LongConcurrentHashMap<long[]>();



    public StorageTrans(File indexFile){
        this(indexFile, false, false, false, false);
    }

    public StorageTrans(File indexFile, boolean disableLocks, boolean deleteFilesAfterClose,
                        boolean readOnly, boolean appendOnly) {
        super(indexFile,  disableLocks, deleteFilesAfterClose, readOnly, appendOnly);
        try{
            writeLock_lock();
            reloadIndexFile();
            if(!inMemory)
                replayLogFile();
        }finally{
            writeLock_unlock();
        }
    }


    protected void reloadIndexFile() {
        transLogOffset = 0;
        writeLock_checkLocked();
        recordLogRefs.clear();
        recordIndexVals.clear();
        longStackPages.clear();
        indexSize = index.getLong(RECID_CURRENT_INDEX_FILE_SIZE *8);
        physSize = index.getLong(RECID_CURRENT_PHYS_FILE_SIZE*8);
        writeLock_checkLocked();
    }

    protected void openLogIfNeeded(){
        if(transLog==null)try{
            FileChannel ch = inMemory ? null :
                    new RandomAccessFile(indexFile.getPath()+TRANS_LOG_FILE_EXT,"rw").getChannel();
            transLog = new ByteBuffer2(inMemory,ch,
                     FileChannel.MapMode.READ_WRITE, "trans");

            transLog.putLong(0, HEADER);
            transLog.putLong(8, 0L);
            transLogOffset = 16;
        }catch(IOException e){
            throw new IOError(e);
        }
    }





    @Override
    public <A> long recordPut(A value, Serializer<A> serializer) {
        try{
            DataOutput2 out = new DataOutput2();
            serializer.serialize(out,value);
            if(CC.ASSERT && out.pos>1<<16) throw new InternalError("Record bigger then 64KB");

            try{
                writeLock_lock();
                //update index file, find free recid
                long recid = longStackTake(RECID_FREE_INDEX_SLOTS);
                if(recid == 0){
                    //could not reuse recid, so create new one
                    if(CC.ASSERT && indexSize%8!=0) throw new InternalError();
                    recid = indexSize/8;
                    indexSize+=8;
                }

                //get physical record
                // first 16 bites is record size, remaining 48 bytes is record offset in phys file
                final long indexValue = out.pos!=0?
                        freePhysRecTake(out.pos):0L;
                writeIndexValToTransLog(recid, indexValue);

                //write new phys data into trans log
                writeOutToTransLog(out, recid, indexValue);

                checkBufferRounding();

                return recid;
            }finally {
                writeLock_unlock();
            }
        }catch(IOException e){
            throw new IOError(e);
        }
    }

    protected void checkBufferRounding() throws IOException {
        if(transLogOffset%ByteBuffer2.BUF_SIZE > ByteBuffer2.BUF_SIZE - MAX_RECORD_SIZE*2){
            //position is to close to end of ByteBuffer (1GB)
            //so start writting into new buffer
            transLog.ensureAvailable(transLogOffset+8);
            transLog.putLong(transLogOffset,WRITE_SKIP_BUFFER);
            transLogOffset += ByteBuffer2.BUF_SIZE-transLogOffset%ByteBuffer2.BUF_SIZE;
        }
    }

    protected void writeIndexValToTransLog(long recid, long indexValue) throws IOException {
        //write new index value into transaction log
        openLogIfNeeded();
        transLog.ensureAvailable(transLogOffset+16);
        transLog.putLong(transLogOffset, WRITE_INDEX_LONG | (recid * 8));
        transLogOffset+=8;
        transLog.putLong(transLogOffset, indexValue);
        transLogOffset+=8;
        recordIndexVals.put(recid,indexValue);
    }

    protected void writeOutToTransLog(DataOutput2 out, long recid, long indexValue) throws IOException {
        openLogIfNeeded();
        transLog.ensureAvailable(transLogOffset+10+out.pos);
        transLog.putLong(transLogOffset, WRITE_PHYS_ARRAY|(indexValue&PHYS_OFFSET_MASK));
        transLogOffset+=8;
        transLog.putUnsignedShort(transLogOffset, out.pos);
        transLogOffset+=2;
        final Long transLogReference = (((long)out.pos)<<48)|transLogOffset;
        recordLogRefs.put(recid, transLogReference); //store reference to transaction log, so we can load data quickly
        transLog.putData(transLogOffset,out);
        transLogOffset+=out.pos;
    }


    @Override
    public <A> A recordGet(long recid, Serializer<A> serializer) {
        try{
            readLock_lock();

            Long indexVal = recordLogRefs.get(recid);
            if(indexVal!=null){
                if(indexVal.longValue() == Long.MIN_VALUE)
                    return null; //was deleted
                //record is in transaction log
                return recordGet2(indexVal, transLog, serializer);
            }else{
                //not in transaction log, read from file
                final long indexValue = index.getLong(recid*8) ;
                return recordGet2(indexValue, phys, serializer);
            }
        }catch(IOException e){
            throw new IOError(e);
        }finally{
            readLock_unlock();
        }
    }

    @Override
    public <A> void recordUpdate(long recid, A value, Serializer<A> serializer) {
        try{
            DataOutput2 out = new DataOutput2();
            serializer.serialize(out,value);

            if(CC.ASSERT && out.pos>1<<16) throw new InternalError("Record bigger then 64KB");
            try{
                writeLock_lock();

                //check if size has changed
                long oldIndexVal = getIndexLong(recid);

                long oldSize = oldIndexVal>>>48;

                if(oldSize == 0 && out.pos==0){
                    //do nothing
                } else if(oldSize == out.pos ){
                    //size is the same, so just write new data
                    writeOutToTransLog(out, recid, oldIndexVal);
                }else if(oldSize != 0 && out.pos==0){
                    //new record has zero size, just delete old phys one
                    freePhysRecPut(oldIndexVal);
                    writeIndexValToTransLog(recid, 0L);
                }else{
                    //size has changed, so write into new location
                    final long newIndexValue = freePhysRecTake(out.pos);

                    writeOutToTransLog(out, recid, newIndexValue);
                    //update index file with new location
                    writeIndexValToTransLog(recid, newIndexValue);

                    //and set old phys record as free
                    if(oldSize!=0)
                        freePhysRecPut(oldIndexVal);
                }

                checkBufferRounding();
            }finally {
                writeLock_unlock();
            }
        }catch(IOException e){
            throw new IOError(e);
        }

    }

    private long getIndexLong(long recid) {
        Long v = recordIndexVals.get(recid);
        return (v!=null) ? v :
             index.getLong(recid * 8);
    }

    @Override
    public void recordDelete(long recid){
        try{
            writeLock_lock();
            openLogIfNeeded();

            transLog.ensureAvailable(transLogOffset+8);
            transLog.putLong(transLogOffset, WRITE_INDEX_LONG_ZERO | (recid*8));
            transLogOffset+=8;
            longStackPut(RECID_FREE_INDEX_SLOTS,recid);
            recordLogRefs.put(recid, Long.MIN_VALUE);
            //check if is in transaction
            long oldIndexVal = getIndexLong(recid);
            recordIndexVals.put(recid,0L);
            if(oldIndexVal!=0)
                freePhysRecPut(oldIndexVal);


            checkBufferRounding();

        }catch(IOException e){
            throw new IOError(e);
        }finally {
            writeLock_unlock();
        }
    }


    @Override
    public void close() {
        super.close();

        try{
            if(transLog!=null){
                transLog.sync();
                transLog.close();
            }
            transLog = null;
            if(deleteFilesOnExit && indexFile!=null){
                new File(indexFile.getPath()+TRANS_LOG_FILE_EXT).delete();
            }

        }catch(IOException e){
            throw new IOError(e);
        }

        //delete log?
    }

    @Override
    public void commit() {
        try{
            writeLock_lock();

            //dump long stack pages
            LongMap.LongMapIterator<long[]> iter = longStackPages.longMapIterator();
            while(iter.moveToNext()){
                transLog.ensureAvailable(transLogOffset+8+2+LONG_STACK_PAGE_SIZE);
                transLog.putLong(transLogOffset, WRITE_PHYS_ARRAY|iter.key());
                transLogOffset+=8;
                transLog.putUnsignedShort(transLogOffset, LONG_STACK_PAGE_SIZE);
                transLogOffset+=2;
                for(long l:iter.value()){
                    transLog.putLong(transLogOffset, l);
                    transLogOffset+=8;
                }
                checkBufferRounding();
            }

            //update physical and logical filesize
            writeIndexValToTransLog(RECID_CURRENT_PHYS_FILE_SIZE, physSize);
            writeIndexValToTransLog(RECID_CURRENT_INDEX_FILE_SIZE, indexSize);


            //seal log file
            transLog.ensureAvailable(transLogOffset+8);
            transLog.putLong(transLogOffset, WRITE_SEAL);
            transLogOffset+=8;
            //flush log file
            transLog.sync();
            //and write mark it was sealed
            transLog.putLong(8, LOG_SEAL);
            transLog.sync();

            replayLogFile();
            reloadIndexFile();

        }catch(IOException e){
            throw new IOError(e);
        }finally{
            writeLock_unlock();
        }
    }

    protected void replayLogFile(){
        try {
            writeLock_checkLocked();
            transLogOffset = 0;

            if(transLog!=null && !inMemory){
                transLog.sync();
                transLog.close();
                transLog = null;
            }

            File logFile = inMemory? null:
                    new File(indexFile.getPath()+TRANS_LOG_FILE_EXT);

            if(!inMemory){
                if(!logFile.exists()){
                    return;
                }
                if(logFile.length()<=16){
                    logFile.delete();
                    return;
                }

                transLog = new ByteBuffer2(false, new RandomAccessFile(logFile,"r").getChannel(),
                        FileChannel.MapMode.READ_ONLY, "trans");
            }



            //read headers
            if(transLog.getLong(0)!=HEADER || transLog.getLong(8) !=LOG_SEAL){
                //wrong headers, discard log
                transLog.close();
                transLog = null;
                if(logFile!=null)
                    logFile.delete();
                return;
            }


            //all good, start replay
            transLogOffset=16;
            long ins = transLog.getLong(transLogOffset);
            transLogOffset+=8;

            while(ins!=WRITE_SEAL && ins!=0){

                final long offset = ins&PHYS_OFFSET_MASK;
                ins -=offset;

                if(ins == WRITE_INDEX_LONG_ZERO){
                    index.ensureAvailable(offset+8);
                    index.putLong(offset, 0L);
                }else if(ins == WRITE_INDEX_LONG){
                    final long value = transLog.getLong(transLogOffset);
                    transLogOffset+=8;
                    index.ensureAvailable(offset+8);
                    index.putLong(offset, value);
                }else if(ins == WRITE_PHYS_LONG){
                    final long value = transLog.getLong(transLogOffset);
                    transLogOffset+=8;
                    phys.ensureAvailable(offset+8);
                    phys.putLong(offset, value);
                }else if(ins == WRITE_PHYS_ARRAY){
                    final int size = transLog.getUnsignedShort(transLogOffset);
                    transLogOffset+=2;
                    //transfer byte[] directly from log file without copying into memory
                    final ByteBuffer blog = transLog.internalByteBuffer(transLogOffset);
                    int pos = (int) (transLogOffset% ByteBuffer2.BUF_SIZE);
                    blog.position(pos);
                    blog.limit(pos+size);
                    phys.ensureAvailable(offset+size);
                    final ByteBuffer bphys = phys.internalByteBuffer(offset);
                    bphys.position((int) (offset% ByteBuffer2.BUF_SIZE));
                    bphys.put(blog);
                    transLogOffset+=size;
                    blog.clear();
                    bphys.clear();
                }else if(ins == WRITE_SKIP_BUFFER){
                    transLogOffset += ByteBuffer2.BUF_SIZE-transLogOffset%ByteBuffer2.BUF_SIZE;
                }else{
                    throw new InternalError("unknown trans log instruction: "+(ins>>>48));
                }

                ins = transLog.getLong(transLogOffset);
                transLogOffset+=8;
            }
            transLogOffset=0;

            //flush dbs
            phys.sync();
            index.sync();
            //and discard log
            transLog.close();
            transLog = null;
            if(logFile!=null)
                logFile.delete();


        } catch (IOException e) {
            throw new IOError(e);
        }

    }


    @Override
    public void rollback() {
        try{
        //discard trans log
        if(transLog!=null){
            transLog.close();
            transLog = null;
            if(indexFile!=null)
                new File(indexFile.getPath()+TRANS_LOG_FILE_EXT).delete();
        }


        }catch(IOException e){
            throw new IOError(e);
        }
        reloadIndexFile();
    }


    private long[] getLongStackPage(final long physOffset, boolean read){
        long[] buf = longStackPages.get(physOffset);
        if(buf == null){
            buf = new long[LONG_STACK_NUM_OF_RECORDS_PER_PAGE+1];
            if(read)
                for(int i=0;i<buf.length;i++){
                    buf[i] = phys.getLong(physOffset+i*8);
                }
            longStackPages.put(physOffset,buf);
        }
        return buf;
    }

    @Override
    protected long longStackTake(final long listRecid) throws IOException {
        final long dataOffset = getIndexLong(listRecid) & PHYS_OFFSET_MASK;
        if(dataOffset == 0)
            return 0; //there is no such list, so just return 0

        writeLock_checkLocked();

        long[] buf = getLongStackPage(dataOffset,true);

        final int numberOfRecordsInPage = (int) (buf[0]>>>(8*7));


        if(CC.ASSERT && numberOfRecordsInPage<=0)
            throw new InternalError();
        if(CC.ASSERT && numberOfRecordsInPage>LONG_STACK_NUM_OF_RECORDS_PER_PAGE) throw new InternalError();

        final long ret = buf[numberOfRecordsInPage];

        final long previousListPhysid = buf[0] & PHYS_OFFSET_MASK;

        //was it only record at that page?
        if(numberOfRecordsInPage == 1){
            //yes, delete this page
            long value = previousListPhysid !=0 ?
                    previousListPhysid | (((long) LONG_STACK_PAGE_SIZE) << 48) :
                    0L;
            //update index so it points to previous (or none)
            writeIndexValToTransLog(listRecid, value);

            //put space used by this page into free list
            longStackPages.remove(dataOffset); //TODO write zeroes to phys file
            freePhysRecPut(dataOffset | (((long)LONG_STACK_PAGE_SIZE)<<48));
        }else{
            //no, it was not last record at this page, so just decrement the counter
            buf[0] = previousListPhysid | ((1L*numberOfRecordsInPage-1L)<<(8*7));
        }
        return ret;

    }

    @Override
    protected void longStackPut(final long listRecid, final long offset) throws IOException {
        writeLock_checkLocked();

        //index position was cleared, put into free index list
        final long listPhysid2 =getIndexLong(listRecid) & PHYS_OFFSET_MASK;

        if(listPhysid2 == 0){ //empty list?
            //yes empty, create new page and fill it with values
            final long listPhysid = freePhysRecTake(LONG_STACK_PAGE_SIZE) &PHYS_OFFSET_MASK;
            long[] buf = getLongStackPage(listPhysid,false);
            if(CC.ASSERT && listPhysid == 0) throw new InternalError();
            //set number of free records in this page to 1
            buf[0] = 1L<<(8*7);
            //set  record
            buf[1] = offset;
            //and update index file with new page location
            writeIndexValToTransLog(listRecid, (((long) LONG_STACK_PAGE_SIZE) << 48) | listPhysid);
        }else{
            long[] buf = getLongStackPage(listPhysid2,true);
            final int numberOfRecordsInPage = (int) (buf[0]>>>(8*7));
            if(numberOfRecordsInPage == LONG_STACK_NUM_OF_RECORDS_PER_PAGE){ //is current page full?
                //yes it is full, so we need to allocate new page and write our number there
                final long listPhysid = freePhysRecTake(LONG_STACK_PAGE_SIZE) &PHYS_OFFSET_MASK;
                long[] bufNew = getLongStackPage(listPhysid,false);
                if(CC.ASSERT && listPhysid == 0) throw new InternalError();
                //final ByteBuffer dataBuf = dataBufs[((int) (listPhysid / BUF_SIZE))];
                //set location to previous page
                //set number of free records in this page to 1
                bufNew[0] = listPhysid2 | (1L<<(8*7));
                //set free record
                bufNew[1] = offset;
                //and update index file with new page location
                writeIndexValToTransLog(listRecid,(((long) LONG_STACK_PAGE_SIZE) << 48) | listPhysid);
            }else{
                //there is space on page, so just write released recid and increase the counter
                buf[1+numberOfRecordsInPage] = offset;
                buf[0] = (buf[0]&PHYS_OFFSET_MASK) | ((1L*numberOfRecordsInPage+1L)<<(8*7));
            }
        }
    }



    protected long freePhysRecTake(final int requiredSize) throws IOException {
        writeLock_checkLocked();

        if(CC.ASSERT && requiredSize<=0) throw new InternalError();

        long freePhysRec = appendOnly? 0L:
                findFreePhysSlot(requiredSize);
        if(freePhysRec!=0){
            return freePhysRec;
        }

        //No free records found, so lets increase the file size.
        //We need to take case of growing ByteBuffers.
        // Also max size of ByteBuffer is 2GB, so we need to use multiple ones

        final long oldFileSize = physSize;
        if(CC.ASSERT && oldFileSize <=0) throw new InternalError("illegal file size:"+oldFileSize);

        //check if new record would be overflowing BUF_SIZE
        if(oldFileSize%ByteBuffer2.BUF_SIZE+requiredSize<=ByteBuffer2.BUF_SIZE){
            //no, so just increase file size
            physSize+=requiredSize;
            //so just increase buffer size

            //and return this
            return (((long)requiredSize)<<48) | oldFileSize;
        }else{
            //new size is overlapping 2GB ByteBuffer size
            //so we need to create empty record for 'padding' size to 2GB

            final long  freeSizeToCreate = ByteBuffer2.BUF_SIZE -  oldFileSize%ByteBuffer2.BUF_SIZE;
            if(CC.ASSERT && freeSizeToCreate == 0) throw new InternalError();

            final long nextBufferStartOffset = oldFileSize + freeSizeToCreate;
            if(CC.ASSERT && nextBufferStartOffset%ByteBuffer2.BUF_SIZE!=0) throw new InternalError();

            //increase the disk size
            physSize += freeSizeToCreate + requiredSize;

            //mark 'padding' free record
            freePhysRecPut((freeSizeToCreate<<48)|oldFileSize);

            //and finally return position at beginning of new buffer
            return (((long)requiredSize)<<48) | nextBufferStartOffset;
        }

    }




}

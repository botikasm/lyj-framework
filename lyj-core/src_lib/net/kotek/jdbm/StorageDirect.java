package net.kotek.jdbm;


import java.io.File;
import java.io.IOError;
import java.io.IOException;

public class StorageDirect extends Storage implements RecordManager {


    public StorageDirect(File indexFile){
        this(indexFile, false, false, false, false);
    }

    public StorageDirect(File indexFile, boolean disableLocks,
                         boolean deleteFilesAfterClose,
                         boolean readOnly, boolean appendOnly) {
        super(indexFile, disableLocks, deleteFilesAfterClose, readOnly, appendOnly);
        if(indexFile!=null && new File(indexFile.getPath()+StorageTrans.TRANS_LOG_FILE_EXT).exists()){
            throw new IllegalAccessError("Log file found. Reopen with transaction enabled, to finish transaction log replay!");
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
                    final long indexSize = index.getLong(RECID_CURRENT_INDEX_FILE_SIZE * 8);
                    if(CC.ASSERT && indexSize%8!=0) throw new InternalError();
                    recid = indexSize/8;
                    //grow buffer if necessary
                    index.ensureAvailable(indexSize+8);
                    index.putLong(RECID_CURRENT_INDEX_FILE_SIZE * 8, indexSize + 8);
                }

                //get physical record
                // first 16 bites is record size, remaining 48 bytes is record offset in phys file
                final long indexValue = out.pos!=0?
                        freePhysRecTake(out.pos):
                        0L;

                phys.putData(indexValue&PHYS_OFFSET_MASK, out);
                index.putLong(recid * 8, indexValue);

                return recid;
            }finally {
                writeLock_unlock();
            }
        }catch(IOException e){
            throw new IOError(e);
        }
    }





    @Override
    public <A> A  recordGet(long recid, Serializer<A> serializer) {
        try{
            try{
                readLock_lock();
                final long indexValue = index.getLong(recid * 8) ;
                return recordGet2(indexValue, phys, serializer);
            }finally{
                readLock_unlock();
            }


        }catch(IOException e){
            throw new IOError(e);
        }
    }



    @Override
    public <A> void recordUpdate(long recid, A value, Serializer<A> serializer){
       try{
           DataOutput2 out = new DataOutput2();
           serializer.serialize(out,value);

           if(CC.ASSERT && out.pos>1<<16) throw new InternalError("Record bigger then 64KB");
           try{
               writeLock_lock();

               //check if size has changed
               final long oldIndexVal = index.getLong(recid * 8);
               final long oldSize = oldIndexVal>>>48;
               if(oldSize == 0 && out.pos==0){
                   //do nothing
               }if(oldSize == out.pos ){
                   //size is the same, so just write new data
                   phys.putData(oldIndexVal&PHYS_OFFSET_MASK, out);
               }else if(oldSize != 0 && out.pos==0){
                   //new record has zero size, just delete old phys one
                   freePhysRecPut(oldIndexVal);
                   index.putLong(recid * 8, 0L);
               }else{
                   //size has changed, so write into new location
                   final long newIndexValue = freePhysRecTake(out.pos);
                   phys.putData(newIndexValue&PHYS_OFFSET_MASK, out);
                   //update index file with new location
                   index.putLong(recid * 8, newIndexValue);

                   //and set old phys record as free
                   if(oldSize!=0)
                        freePhysRecPut(oldIndexVal);
               }
           }finally {
               writeLock_unlock();
           }
       }catch(IOException e){
           throw new IOError(e);
       }

   }


   @Override
   public void recordDelete(long recid){
        try{
            writeLock_lock();
            final long oldIndexVal = index.getLong(recid * 8);
            index.putLong(recid * 8, 0L);
            longStackPut(RECID_FREE_INDEX_SLOTS,recid);
            if(oldIndexVal!=0)
                freePhysRecPut(oldIndexVal);
        }catch(IOException e){
            throw new IOError(e);
        }finally {
            writeLock_unlock();
        }
    }

    @Override
    public void commit() {
        //TODO sync here?
    }

    @Override
    public void rollback() {
        throw new IllegalAccessError("Can not rollback, transactions disabled.");
    }


    @Override
   protected long longStackTake(final long listRecid) throws IOException {
        final long dataOffset = index.getLong(listRecid * 8) &PHYS_OFFSET_MASK;
        if(dataOffset == 0)
            return 0; //there is no such list, so just return 0

        writeLock_checkLocked();


        final int numberOfRecordsInPage = phys.getUnsignedByte(dataOffset);

        if(CC.ASSERT && numberOfRecordsInPage<=0) throw new InternalError();
        if(CC.ASSERT && numberOfRecordsInPage>LONG_STACK_NUM_OF_RECORDS_PER_PAGE) throw new InternalError();

        final long ret = phys.getLong (dataOffset+numberOfRecordsInPage*8);

        //was it only record at that page?
        if(numberOfRecordsInPage == 1){
            //yes, delete this page
            final long previousListPhysid =phys.getLong(dataOffset) &PHYS_OFFSET_MASK;
            if(previousListPhysid !=0){
                //update index so it points to previous page
                index.putLong(listRecid * 8, previousListPhysid | (((long) LONG_STACK_PAGE_SIZE) << 48));
            }else{
                //zero out index
                index.putLong(listRecid * 8, 0L);
            }
            //put space used by this page into free list
            freePhysRecPut(dataOffset | (((long)LONG_STACK_PAGE_SIZE)<<48));
        }else{
            //no, it was not last record at this page, so just decrement the counter
            phys.putUnsignedByte(dataOffset, (byte) (numberOfRecordsInPage - 1));
        }
        return ret;

    }

   @Override
   protected void longStackPut(final long listRecid, final long offset) throws IOException {
       writeLock_checkLocked();

       //index position was cleared, put into free index list
        final long listPhysid2 = index.getLong(listRecid * 8) &PHYS_OFFSET_MASK;

        if(listPhysid2 == 0){ //empty list?
            //yes empty, create new page and fill it with values
            final long listPhysid = freePhysRecTake(LONG_STACK_PAGE_SIZE) &PHYS_OFFSET_MASK;
            if(CC.ASSERT && listPhysid == 0) throw new InternalError();
            //set previous Free Index List page to zero as this is first page
            phys.putLong(listPhysid, 0L);
            //set number of free records in this page to 1
            phys.putUnsignedByte(listPhysid, (byte) 1);

            //set  record
            phys.putLong(listPhysid + 8, offset);
            //and update index file with new page location
            index.putLong(listRecid * 8, (((long) LONG_STACK_PAGE_SIZE) << 48) | listPhysid);
        }else{
            final int numberOfRecordsInPage = phys.getUnsignedByte(listPhysid2);
            if(numberOfRecordsInPage == LONG_STACK_NUM_OF_RECORDS_PER_PAGE){ //is current page full?
                //yes it is full, so we need to allocate new page and write our number there

                final long listPhysid = freePhysRecTake(LONG_STACK_PAGE_SIZE) &PHYS_OFFSET_MASK;
                if(CC.ASSERT && listPhysid == 0) throw new InternalError();
                //final ByteBuffer dataBuf = dataBufs[((int) (listPhysid / BUF_SIZE))];
                //set location to previous page
                phys.putLong(listPhysid, listPhysid2);
                //set number of free records in this page to 1
                phys.putUnsignedByte(listPhysid, (byte) 1);
                //set free record
                phys.putLong(listPhysid +  8, offset);
                //and update index file with new page location
                index.putLong(listRecid * 8, (((long) LONG_STACK_PAGE_SIZE) << 48) | listPhysid);
            }else{
                //there is space on page, so just write released recid and increase the counter
                phys.putLong(listPhysid2 +  8 + 8 * numberOfRecordsInPage, offset);
                phys.putUnsignedByte(listPhysid2, (byte) (numberOfRecordsInPage + 1));
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

        final long physFileSize = index.getLong(RECID_CURRENT_PHYS_FILE_SIZE*8);
        if(CC.ASSERT && physFileSize <=0) throw new InternalError("illegal file size:"+physFileSize);

        //check if new record would be overflowing BUF_SIZE
        if(physFileSize%ByteBuffer2.BUF_SIZE+requiredSize<=ByteBuffer2.BUF_SIZE){
            //no, so just increase file size
            phys.ensureAvailable(physFileSize+requiredSize);
            //so just increase buffer size
            index.putLong(RECID_CURRENT_PHYS_FILE_SIZE * 8, physFileSize + requiredSize);

            //and return this
            return (((long)requiredSize)<<48) | physFileSize;
        }else{
            //new size is overlapping 2GB ByteBuffer size
            //so we need to create empty record for 'padding' size to 2GB

            final long  freeSizeToCreate = ByteBuffer2.BUF_SIZE -  physFileSize%ByteBuffer2.BUF_SIZE;
            if(CC.ASSERT && freeSizeToCreate == 0) throw new InternalError();

            final long nextBufferStartOffset = physFileSize + freeSizeToCreate;
            if(CC.ASSERT && nextBufferStartOffset%ByteBuffer2.BUF_SIZE!=0) throw new InternalError();

            //increase the disk size
            phys.ensureAvailable(physFileSize + freeSizeToCreate + requiredSize);
            index.putLong(RECID_CURRENT_PHYS_FILE_SIZE * 8, physFileSize + freeSizeToCreate + requiredSize);

            //mark 'padding' free record
            freePhysRecPut((freeSizeToCreate<<48)|physFileSize);

            //and finally return position at beginning of new buffer
            return (((long)requiredSize)<<48) | nextBufferStartOffset;
        }

    }



}
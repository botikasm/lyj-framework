package net.kotek.jdbm;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * A builder class for creating and opening a database.
 */
public class DBMaker {


    protected static final byte CACHE_DISABLE = 0;
    protected static final byte CACHE_FIXED_HASH_TABLE = 1;
    protected static final byte CACHE_HARD_REF = 2;
    protected static final byte CACHE_WEAK_REF = 3;
    protected static final byte CACHE_SOFT_REF = 4;


    protected byte _cache = CACHE_FIXED_HASH_TABLE;
    protected int _cacheSize = 1024*32;

    /** file to open, if null opens in memory store */
    protected File _file;

    protected boolean _transactionsEnabled = true;

    protected boolean _asyncWriteEnabled = true;
    protected boolean _asyncSerializationEnabled = true;

    protected boolean _deleteFilesAfterClose = false;
    protected boolean _readOnly = false;
    protected boolean _closeOnJvmShutdown = false;

    protected boolean _compressionEnabled = false;

    protected byte[] _xteaEncryptionKey = null;

    protected boolean _appendOnlyEnabled = false;

    protected boolean _checksumEnabled = false;


    /** use static factory methods, or make subclass */
    protected DBMaker(){}

    /** Creates new in-memory database. Changes are lost after JVM exits*/
    public static DBMaker newMemoryDB(){
        DBMaker m = new DBMaker();
        m._file = null;
        return  m;
    }


    /**
     * Creates new database in temporary folder.
     *
     * @return
     */
    public static DBMaker newTempFileDB() {
        try {
            return newFileDB(File.createTempFile("JDBM-temp","db"));
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    /** Creates or open database stored in file. */
    public static DBMaker newFileDB(File file){
        DBMaker m = new DBMaker();
        m._file = file;
        return  m;
    }


    /**
     * Transactions are enabled by default (but not implemented yet).
     * You must call <db>DB.commit()</db> to save your changes.
     * It is possible to disable transactions for better write performance
     * In this case all integrity checks are sacrificed for faster speed.
     * If transactions are disabled, you must call DB.close() method before exit,
     * otherwise your store <b>WILL BE CORRUPTED</b>
     *
     * @return this builder
     */
    public DBMaker transactionDisable(){
        this._transactionsEnabled = false;
        return this;
    }

    /**
     * Instance cache is enabled by default.
     * This greatly decreases serialization overhead and improves performance.
     * Call this method to disable instance cache, so an object will always be deserialized.
     * <p/>
     * This may workaround some problems
     *
     * @return this builder
     */
    public DBMaker cacheDisable(){
        this._cache = CACHE_DISABLE;
        return this;
    }

    /**
     * Enables unbounded hard reference cache.
     * This cache is good if you have lot of available memory.
     * <p/>
     * All fetched records are added to HashMap and stored with hard reference.
     * To prevent OutOfMemoryExceptions JDBM monitors free memory,
     * if it is bellow 25% cache is cleared.
     *
     * @return this builder
     */
    public DBMaker cacheHardRefEnable(){
        this._cache = CACHE_HARD_REF;
        return this;
    }


    /**
     * Enables unbounded cache which uses <code>WeakReference</code>.
     * Items are removed from cache by Garbage Collector
     *
     * @return this builder
     */
    public DBMaker cacheWeakRefEnable(){
        this._cache = CACHE_WEAK_REF;
        return this;
    }

    /**
     * Enables unbounded cache which uses <code>SoftReference</code>.
     * Items are removed from cache by Garbage Collector
     *
     * @return this builder
     */
    public DBMaker cacheSoftRefEnable(){
        this._cache = CACHE_SOFT_REF;
        return this;
    }



    /**
     * Set cache size. Interpretations depends on cache type.
     * For fixed size caches (such as FixedHashTable cache) it is maximal number of items in cache.
     * <p/>
     * For unbounded caches (such as HardRef cache) it is initial capacity of underlying table (HashMap).
     * <p/>
     * Default cache size is 32768.
     *
     * @param cacheSize new cache size
     * @return this builder
     */
    public DBMaker cacheSize(int cacheSize){
        this._cacheSize = cacheSize;
        return this;
    }


    /**
     * By default all modifications are queued and written into disk on Background Writer Thread.
     * So all modifications are performed in asynchronous mode and do not block.
     * <p/>
     * It is possible to disable Background Writer Thread, but this greatly hurts concurrency.
     * Without async writes, all threads blocks until all previous writes are not finished (single big lock).
     *
     * <p/>
     * This may workaround some problems
     *
     * @return this builder
     */
    public DBMaker asyncWriteDisable(){
        this._asyncWriteEnabled = false;
        return this;
    }

    /**
     * By default all objects are serialized in Background Writer Thread.
     * <p/>
     * This may improve performance. For example with single thread access, Async Serialization offloads
     * lot of work to second core. Or when multiple values are added into single tree node,
     * node has to be serialized only once. Without Async Serialization node is serialized each time
     * node is updated.
     * <p/>
     * On other side Async Serialization moves all serialization into single thread. This
     * hurts performance with many concurrent-independent updates.
     * <p/>
     * Async Serialization may also produce some unexpected results when your data classes are not
     * immutable. Consider example bellow. If Async Serialization is disabled, it always prints 'Peter'.
     * If it is enabled (by default) it creates race condition and randomly prints 'Peter' or 'Jack',
     * <pre>
     *     Person person = new Person();
     *     person.setName("Peter");
     *     map.put(id, person)
     *     person.setName("Jack");
     *     //long pause
     *     println(map.get(id).getName());
     * </pre>
     *
     * <p/>
     * This may also workaround some problems
     *
     * @return this builder
     */
    public DBMaker asyncSerializationDisable(){
        this._asyncSerializationEnabled = false;
        return this;
    }


    /**
     * Try to delete files after DB is closed.
     * File deletion may silently fail, especially on Windows where buffer needs to be unmapped file delete.
     *
     * @return this builder
     */
    public DBMaker deleteFilesAfterClose(){
        this._deleteFilesAfterClose = true;
        return this;
    }

    /**
     * Adds JVM shutdown hook and closes DB just before JVM;
     *
     * @return this builder
     */
    public DBMaker closeOnJvmShutdown(){
        this._closeOnJvmShutdown = true;
        return this;
    }

    /**
     * Enables record compression.
     * <p/>
     * Make sure you enable this every time you reopen store, otherwise record de-serialization fails unpredictably.
     *
     * @return this builder
     */
    public DBMaker compressionEnable(){
        this._compressionEnabled = true;
        return this;
    }


    /**
     * Encrypt storage using XTEA algorithm.
     * <p/>
     * XTEA is sound encryption algorithm. However implementation in JDBM was not peer-reviewed.
     * JDBM only encrypts records data, so attacker may see number of records and their sizes.
     * <p/>
     * Make sure you enable this every time you reopen store, otherwise record de-serialization fails unpredictably.
     *
     * @param password for encryption
     * @return this builder
     */
    public DBMaker encryptionEnable(String password){
        try {
            return encryptionEnable(password.getBytes(JdbmUtil.UTF8));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Encrypt storage using XTEA algorithm.
     * <p/>
     * XTEA is sound encryption algorithm. However implementation in JDBM was not peer-reviewed.
     * JDBM only encrypts records data, so attacker may see number of records and their sizes.
     * <p/>
     * Make sure you enable this every time you reopen store, otherwise record de-serialization fails unpredictably.
     *
     * @param password for encryption
     * @return this builder
     */
    public DBMaker encryptionEnable(byte[] password){
        _xteaEncryptionKey = password;
        return this;
    }


    /**
     * Adds CRC32 checksum at end of each record to check data integrity.
     * It throws 'IOException("CRC32 does not match, data broken")' on de-serialization if data are corrupted
     * <p/>
     * Make sure you enable this every time you reopen store, otherwise record de-serialization fails.
     *
     * @return this builder
     */
    public DBMaker checksumEnable(){
        this._checksumEnabled = true;
        return this;
    }


    /**
     * Open store in read-only mode. Any modification attempt will throw
     * <code>UnsupportedOperationException("Read-only")</code>
     *
     * @return this builder
     */
    public DBMaker readOnly(){
        this._readOnly = true;
        return this;
    }



    /**
     * In 'appendOnly' mode existing free space is not reused,
     * but records are added to the end of the store.
     * <p/>
     * This slightly improves write performance as store does not have
     * to traverse list of free records to find and reuse existing position.
     * <p/>
     * It also decreases chance for store corruption, as existing data
     * are not overwriten with new record.
     * <p/>
     * When this mode is used for longer time, store becomes fragmented.
     * It is necessary to run defragmentation then.
     *
     * @return this builder
     */
    public DBMaker appendOnlyEnable(){
        this._appendOnlyEnabled = true;
        return this;
    }






    /** constructs DB using current settings */
    public DB make(){


        if(_readOnly && _file==null)
            throw new UnsupportedOperationException("Can not open in-memory DB in read-only mode.");

        if(_readOnly && !_file.exists()){
            throw new UnsupportedOperationException("Can not open non-existing file in read-only mode.");
        }

        RecordManager recman = _transactionsEnabled?
                new StorageTrans(_file, _asyncWriteEnabled, _deleteFilesAfterClose,_readOnly, _appendOnlyEnabled):
                new StorageDirect(_file, _asyncWriteEnabled, _deleteFilesAfterClose,_readOnly, _appendOnlyEnabled);

        if(_asyncWriteEnabled && !_readOnly)
            recman = new AsyncWriteWrapper(recman, _asyncSerializationEnabled);

        if(_checksumEnabled){
            recman = new ByteTransformWrapper(recman, new ChecksumCRC32Serializer());
        }

        if(_xteaEncryptionKey!=null){
            recman = new ByteTransformWrapper(recman, new EncryptionXTEA(_xteaEncryptionKey));
        }


        if(_compressionEnabled){
            recman = new ByteTransformWrapper(recman, new CompressLZFSerializer());
        }



        if(_cache == CACHE_DISABLE){
            //do not wrap recman in cache
        }if(_cache == CACHE_FIXED_HASH_TABLE){
            recman = new CacheHashTable(recman,_cacheSize);
        }else if (_cache == CACHE_HARD_REF){
            recman = new CacheHardRef(recman,_cacheSize);
        }else if (_cache == CACHE_WEAK_REF){
            recman = new CacheWeakSoftRef(recman,true);
        }else if (_cache == CACHE_SOFT_REF){
            recman = new CacheWeakSoftRef(recman,false);
        }

       if(_readOnly)
            recman = new ReadOnlyWrapper(recman);

        final DB db = new DB(recman);
        if(_closeOnJvmShutdown){
            Runtime.getRuntime().addShutdownHook(new Thread("JDBM shutdown") {
                public void run() {
                    if (db.recman != null) {
                        db.close();
                    }
                }
            });
        }

        return db;
    }


}

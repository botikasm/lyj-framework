package net.kotek.jdbm;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Provides serialization and deserialization
 */
public interface Serializer<A> extends SerializationHeader{

    /**
     * Serialize the content of an object into a ObjectOutput
     *
     * @param out ObjectOutput to save object into
     * @param value Object to serialize
     */
    public void serialize(DataOutput out, A value)
            throws IOException;


    /**
     * Deserialize the content of an object from a DataInput.
     *
     * @param in to read serialized data from
     * @param available how many bytes are available in DataInput for reading, may be -1 (in streams) or 0 (null).
     * @return deserialized object
     * @throws IOException
     */
    public A deserialize(DataInput in, int available)
            throws IOException;

    /**
     * Serializes strings using UTF8 encoding.
     * Used mainly for testing.
     * Does not handle null values.
     */
    Serializer<String> STRING_SERIALIZER = new Serializer<String>() {

        public void serialize(DataOutput out, String value) throws IOException {
            final byte[] bytes = value.getBytes(JdbmUtil.UTF8);
            out.write(bytes);
        }


        public String deserialize(DataInput in, int available) throws IOException {
            byte[] bytes = new byte[available];
            in.readFully(bytes);
            return new String(bytes,JdbmUtil.UTF8);
        }
    };




    /** Serializes Long into 8 bytes, used mainly for testing.
     * Does not handle null values.*/
    Serializer<Long> LONG_SERIALIZER = new Serializer<Long>() {
        @Override
        public void serialize(DataOutput out, Long value) throws IOException {
            out.writeLong(value);
        }

        @Override
        public Long deserialize(DataInput in, int available) throws IOException {
            return in.readLong();
        }
    };

    /** Serializes Integer into 4 bytes, used mainly for testing.
     * Does not handle null values.*/
    Serializer<Integer> INTEGER_SERIALIZER = new Serializer<Integer>() {
        @Override
        public void serialize(DataOutput out, Integer value) throws IOException {
            out.writeInt(value);
        }

        @Override
        public Integer deserialize(DataInput in, int available) throws IOException {
            return in.readInt();
        }
    };


    Serializer<byte[] > BYTE_ARRAY_SERIALIZER = new Serializer<byte[]>() {

        @Override
        public void serialize(DataOutput out, byte[] value) throws IOException {
            out.write(value);
        }

        @Override
        public byte[] deserialize(DataInput in, int available) throws IOException {
            byte[] ret = new byte[available];
            in.readFully(ret);
            return ret;
        }
    } ;


    /** always writes zero length data, and always deserializes it as null */
    Serializer<Object> NULL_SERIALIZER = new Serializer<Object>() {
        @Override
        public void serialize(DataOutput out, Object value) throws IOException {
        }

        @Override
        public Object deserialize(DataInput in, int available) throws IOException {
            return null;
        }
    };

    /** basic serializer for most classes in 'java.lang' and 'java.util' packages*/
    @SuppressWarnings("unchecked")
    Serializer<Object> BASIC_SERIALIZER = new SerializerBase();


}


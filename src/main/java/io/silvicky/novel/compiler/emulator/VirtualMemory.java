package io.silvicky.novel.compiler.emulator;

import io.silvicky.novel.compiler.types.PrimitiveType;

import java.io.*;

public class VirtualMemory
{
    private static final byte[] mem=new byte[1048576];

    public static void writeToMemory(int target, Object o)
    {
        try(ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        DataOutputStream dataOutputStream=new DataOutputStream(byteArrayOutputStream))
        {
            switch (o)
            {
                case Integer integer -> dataOutputStream.writeInt(integer);
                case Long lon -> dataOutputStream.writeLong(lon);
                case Boolean bool -> dataOutputStream.writeBoolean(bool);
                case Character cha -> dataOutputStream.writeChar(cha);
                case Short sho -> dataOutputStream.writeShort(sho);
                case Double db -> dataOutputStream.writeDouble(db);
                case Float flt -> dataOutputStream.writeFloat(flt);
                case Byte bt -> dataOutputStream.writeByte(bt);
                case null, default -> throw new RuntimeException();
            }
            dataOutputStream.flush();
            byte[] bytes= byteArrayOutputStream.toByteArray();
            if (dataOutputStream.size() >= 0) System.arraycopy(bytes, 0, mem, target, dataOutputStream.size());
        }
        catch (IOException e){throw new RuntimeException(e);}
    }

    public static Object readFromMemory(int address, PrimitiveType type)
    {
        try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(mem,address, type.getSize());
            DataInputStream dataInputStream=new DataInputStream(byteArrayInputStream))
        {
            switch (type)
            {
                case BOOL ->
                {
                    return dataInputStream.readBoolean();
                }
                case CHAR, UNSIGNED_CHAR ->
                {
                    return dataInputStream.readByte();
                }
                case SHORT, UNSIGNED_SHORT ->
                {
                    return dataInputStream.readShort();
                }
                case INT, LONG, UNSIGNED_INT, UNSIGNED_LONG ->
                {
                    return dataInputStream.readInt();
                }
                case LONG_LONG, UNSIGNED_LONG_LONG ->
                {
                    return dataInputStream.readLong();
                }
                case FLOAT ->
                {
                    return dataInputStream.readFloat();
                }
                case DOUBLE,LONG_DOUBLE ->
                {
                    return dataInputStream.readDouble();
                }
                default -> throw new RuntimeException();
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void moveBytes(int target, int source, int bytes)
    {
        for(int i=0;i<bytes;i++)mem[target+i]=mem[source+i];
    }
}

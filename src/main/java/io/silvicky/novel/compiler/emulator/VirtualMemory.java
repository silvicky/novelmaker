package io.silvicky.novel.compiler.emulator;

import io.silvicky.novel.compiler.types.PrimitiveType;

public class VirtualMemory
{
    private static final long[] mem=new long[1048576];

    public static void writeToMemory(int target, Object o)
    {
        //TODO Remove this
        switch (o)
        {
            case Integer integer -> mem[target] = integer;
            case Long lon -> mem[target] = lon;
            case Boolean bool -> mem[target] = bool ? 1 : 0;
            case Character cha -> mem[target] = cha;
            case Short sho -> mem[target] = sho;
            case null, default -> throw new RuntimeException();
        }
    }

    public static Object readFromMemory(int address, PrimitiveType type)
    {
        //TODO this is also wrong
        switch (type)
        {
            case BOOL ->
            {
                return mem[address]!=0;
            }
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte)mem[address];
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)mem[address];
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)mem[address];
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return mem[address];
            }
            default -> throw new RuntimeException();
        }
    }

    public static void moveBytes(int target, int source, int bytes)
    {
        for(int i=0;i<bytes;i++)mem[target+i]=mem[source+i];
    }
}

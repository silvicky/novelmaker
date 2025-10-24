package io.silvicky.novel.compiler.types;

public enum PrimitiveType implements Type
{
    AUTO(0,null,null,"auto"),
    VOID(0,null,null,"void"),
    ELLIPSIS(0,null,null,"..."),
    BOOL(1, Boolean.class,Boolean.TYPE, "bool"),//bool is int in C but not here
    CHAR(1, Byte.class, Byte.TYPE,"char"),
    UNSIGNED_CHAR(1, Byte.class, Byte.TYPE,"unsigned char"),
    SHORT(2, Short.class, Short.TYPE,"short"),
    UNSIGNED_SHORT(2, Short.class, Short.TYPE,"unsigned short"),
    INT(4, Integer.class, Integer.TYPE,"int"),
    UNSIGNED_INT(4, Integer.class, Integer.TYPE, "unsigned int"),
    LONG(4, Integer.class, Integer.TYPE, "long"),
    UNSIGNED_LONG(4, Integer.class, Integer.TYPE, "unsigned long"),
    LONG_LONG(8, Long.class, Long.TYPE, "long long"),
    UNSIGNED_LONG_LONG(8, Long.class,Long.TYPE, "unsigned long long"),
    FLOAT(4, Float.class,Float.TYPE, "float"),
    DOUBLE(8, Double.class, Double.TYPE,"double"),
    LONG_DOUBLE(8, Double.class, Double.TYPE,"long double");
    private final int size;
    public final Class<?> javaType;
    public final Class<?> javaPrimitiveType;
    public final String symbol;
    PrimitiveType(int size, Class<?> javaType,Class<?> javaPrimitiveType, String symbol)
    {
        this.size = size;
        this.javaType = javaType;
        this.javaPrimitiveType=javaPrimitiveType;
        this.symbol=symbol;
    }
    public boolean isInteger()
    {
        return !(javaType.equals(Float.class)||javaType.equals(Double.class));
    }
    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public boolean isAuto()
    {
        return this==AUTO;
    }
    public static PrimitiveType getPrimitiveTypeByJava(Class<?> clazz)
    {
        for(PrimitiveType type:values())if(clazz.equals(type.javaType)||clazz.equals(type.javaPrimitiveType))return type;
        throw new RuntimeException("unknown primitive type");
    }
}

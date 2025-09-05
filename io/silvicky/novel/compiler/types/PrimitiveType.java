package io.silvicky.novel.compiler.types;

public enum PrimitiveType implements Type
{
    BOOL(1, Boolean.class, "bool"),
    CHAR(1, Character.class, "char"),
    UNSIGNED_CHAR(1, Character.class, "unsigned char"),
    SHORT(2, Short.class, "short"),
    UNSIGNED_SHORT(2, Short.class, "unsigned short"),
    INT(4, Integer.class, "int"),
    UNSIGNED_INT(4, Integer.class, "unsigned int"),
    LONG(4, Integer.class, "long"),
    UNSIGNED_LONG(4, Integer.class, "unsigned long"),
    LONG_LONG(8, Long.class, "long long"),
    UNSIGNED_LONG_LONG(8, Long.class, "unsigned long long"),
    FLOAT(4, Float.class, "float"),
    DOUBLE(8, Double.class, "double"),
    LONG_DOUBLE(8, Double.class, "long double");
    private final int size;
    public final Class<?> javaType;
    public final String symbol;
    PrimitiveType(int size, Class<?> javaType, String symbol)
    {
        this.size = size;
        this.javaType = javaType;
        this.symbol=symbol;
    }
    public boolean isInteger()
    {
        return !(javaType.equals(Float.class)||javaType.equals(Double.class));
    }
    @Override
    public int getSize()
    {
        //TODO
        return 1;
        //return size;
    }
}

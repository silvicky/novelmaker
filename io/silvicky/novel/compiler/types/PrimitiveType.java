package io.silvicky.novel.compiler.types;

import io.silvicky.novel.compiler.tokens.KeywordToken;

public enum PrimitiveType implements Type
{
    BOOL(1, Boolean.class),
    CHAR(1, Character.class),
    UNSIGNED_CHAR(1, Character.class),
    SHORT(2, Short.class),
    UNSIGNED_SHORT(2, Short.class),
    INT(4, Integer.class),
    UNSIGNED_INT(4, Integer.class),
    LONG(4, Integer.class),
    UNSIGNED_LONG(4, Integer.class),
    LONG_LONG(8, Long.class),
    UNSIGNED_LONG_LONG(8, Long.class),
    FLOAT(4, Float.class),
    DOUBLE(8, Double.class),
    LONG_DOUBLE(8, Double.class);
    private final int size;
    private final Class<?> javaType;
    PrimitiveType(int size, Class<?> javaType)
    {
        this.size = size;
        this.javaType = javaType;
    }

    @Override
    public int getSize()
    {
        return 0;
    }
}

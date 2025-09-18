package io.silvicky.novel.compiler.types;

public interface Type
{
    PrimitiveType ADDRESS_TYPE=PrimitiveType.INT;
    int ADDRESS_WIDTH= ADDRESS_TYPE.getSize();
    int getSize();
    boolean isAuto();
}

package io.silvicky.novel.compiler.types;

public interface Type
{
    //TODO
    int ADDRESS_WIDTH=1;
    PrimitiveType ADDRESS_TYPE=PrimitiveType.INT;
    int getSize();
}

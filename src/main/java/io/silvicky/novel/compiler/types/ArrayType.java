package io.silvicky.novel.compiler.types;

public record ArrayType(Type baseType,int size) implements Type,AbstractPointer
{
    @Override
    public int getSize()
    {
        return baseType.getSize()*size;
    }
}

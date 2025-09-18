package io.silvicky.novel.compiler.types;

public record ArrayType(Type baseType,int size) implements Type,AbstractPointer
{
    @Override
    public int getSize()
    {
        return baseType.getSize()*size;
    }

    @Override
    public boolean isAuto()
    {
        return baseType().isAuto();
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof ArrayType arrayType&& baseType.equals(arrayType.baseType);
    }
}

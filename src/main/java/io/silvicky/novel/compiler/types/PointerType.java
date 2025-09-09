package io.silvicky.novel.compiler.types;

public record PointerType(Type baseType) implements Type,AbstractPointer
{
    @Override
    public int getSize()
    {
        return Type.ADDRESS_WIDTH;
    }
    @Override
    public boolean equals(Object o)
    {
        return o instanceof PointerType pointerType&& baseType.equals(pointerType.baseType);
    }
}

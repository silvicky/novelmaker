package io.silvicky.novel.compiler.types;

public record PointerType(Type baseType) implements Type,AbstractPointer
{
    @Override
    public int getSize()
    {
        return Type.ADDRESS_WIDTH;
    }
}

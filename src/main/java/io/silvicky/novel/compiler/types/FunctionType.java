package io.silvicky.novel.compiler.types;

import java.util.List;

public record FunctionType(Type returnType, List<Type> args) implements Type,AbstractPointer
{
    @Override
    public int getSize()
    {
        return Type.ADDRESS_WIDTH;
    }

    @Override
    public Type baseType()
    {
        return this;
    }
}

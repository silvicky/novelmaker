package io.silvicky.novel.compiler.types;

import java.util.List;

public record FunctionType(Type returnType, List<Type> params) implements Type,AbstractPointer
{
    @Override
    public int getSize()
    {
        return Type.ADDRESS_WIDTH;
    }

    @Override
    public boolean isAuto()
    {
        return returnType().isAuto();
    }

    @Override
    public Type baseType()
    {
        return this;
    }
    @Override
    public boolean equals(Object o)
    {
        return o instanceof FunctionType functionType&& params.equals(functionType.params)&&returnType.equals(functionType.returnType);
    }
}

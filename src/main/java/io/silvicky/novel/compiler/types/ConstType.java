package io.silvicky.novel.compiler.types;

public record ConstType(Type baseType) implements Type
{
    @Override
    public int getSize()
    {
        return baseType.getSize();
    }

    @Override
    public boolean isAuto()
    {
        return baseType().isAuto();
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof ConstType constType&&baseType.equals(constType.baseType);
    }
}

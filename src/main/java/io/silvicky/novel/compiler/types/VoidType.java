package io.silvicky.novel.compiler.types;

public class VoidType implements Type
{
    @Override
    public int getSize() {return 0;}
    @Override
    public boolean equals(Object o)
    {
        return o instanceof VoidType;
    }
}

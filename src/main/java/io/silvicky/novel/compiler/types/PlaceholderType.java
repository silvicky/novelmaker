package io.silvicky.novel.compiler.types;

public record PlaceholderType(String id) implements Type
{
    @Override
    public int getSize()
    {
        return 0;
    }

    @Override
    public boolean isAuto()
    {
        return false;
    }
}

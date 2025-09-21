package io.silvicky.novel.compiler.code.primitive;

public record PopCodeP(int size) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("pop (size=%d)",
                size);
    }
}

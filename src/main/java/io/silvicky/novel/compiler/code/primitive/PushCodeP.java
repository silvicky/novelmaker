package io.silvicky.novel.compiler.code.primitive;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record PushCodeP(int source, int size) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("push %s(size=%d)",
                lookupVariableName(source),
                size);
    }
}

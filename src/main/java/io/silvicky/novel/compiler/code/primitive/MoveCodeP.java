package io.silvicky.novel.compiler.code.primitive;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record MoveCodeP(int target, int source, int size) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("%s=%s(size=%d)",
                lookupVariableName(target),
                lookupVariableName(source),
                size);
    }
}

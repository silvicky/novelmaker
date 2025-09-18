package io.silvicky.novel.compiler.code.primitive;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record FetchReturnValueCodeP(int target, int size) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("%s=RET(size=%d)",
                lookupVariableName(target),
                size);
    }
}

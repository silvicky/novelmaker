package io.silvicky.novel.compiler.code.primitive;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record CallCodeP(int target) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("call %s",
                lookupVariableName(target));
    }
}

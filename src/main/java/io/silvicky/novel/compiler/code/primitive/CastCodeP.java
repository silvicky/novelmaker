package io.silvicky.novel.compiler.code.primitive;

import io.silvicky.novel.compiler.types.PrimitiveType;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record CastCodeP(int target, int source, PrimitiveType targetType, PrimitiveType sourceType) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("%s(%s)=%s(%s)",lookupVariableName(target),targetType.symbol,lookupVariableName(source),sourceType.symbol);
    }
}

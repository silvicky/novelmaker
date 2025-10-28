package io.silvicky.novel.compiler.code.primitive;

import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record AssignMCodeP(int target, int left, PrimitiveType type, OperatorType op) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("%s=%s%s(%s)",lookupVariableName(target),op.symbol,lookupVariableName(left),type.symbol);
    }
}

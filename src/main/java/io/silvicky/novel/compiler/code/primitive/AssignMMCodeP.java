package io.silvicky.novel.compiler.code.primitive;

import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record AssignMMCodeP(int target, int left, int right, PrimitiveType type, OperatorType op) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("%s=%s%s%s(%s)",lookupVariableName(target),lookupVariableName(left),op.symbol,lookupVariableName(right),type.symbol);
    }
}

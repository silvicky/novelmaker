package io.silvicky.novel.compiler.code.primitive;

import io.silvicky.novel.compiler.types.PrimitiveType;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record AssignICodeP(int target, Object left) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("%s=%s(%s)",lookupVariableName(target),left,PrimitiveType.getPrimitiveTypeByJava(left.getClass()).symbol);
    }
}

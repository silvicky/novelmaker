package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.code.primitive.PrimitiveTypeCode;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record LeaCode(int target, int base, int offset) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("%s=&(%s)+%d",lookupVariableName(target),lookupVariableName(base),offset);
    }
}

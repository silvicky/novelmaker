package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.types.Type;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record IndirectAssignCode(int target, int left, Type targetType) implements Code
{
    public String toString()
    {
        return "*"+lookupVariableName(target)+"="+lookupVariableName(left);
    }
}

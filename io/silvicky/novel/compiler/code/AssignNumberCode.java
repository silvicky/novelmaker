package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.types.Type;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record AssignNumberCode(int target, Object left, Type targetType,Type leftType) implements Code
{
    public String toString()
    {
        return lookupVariableName(target)+"="+left;
    }
}

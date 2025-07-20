package io.silvicky.novel.compiler.code;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record AssignNumberCode(int target, long left) implements Code
{
    public String toString()
    {
        return lookupVariableName(target)+"="+left;
    }
}

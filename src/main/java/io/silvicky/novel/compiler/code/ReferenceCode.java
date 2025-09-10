package io.silvicky.novel.compiler.code;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record ReferenceCode(int target, int left) implements Code
{
    @Override
    public String toString()
    {
        return String.format("%s=&(%s)",lookupVariableName(target),lookupVariableName(left));
    }
}

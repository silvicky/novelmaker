package io.silvicky.novel.compiler.code.primitive;

import static io.silvicky.novel.compiler.Compiler.lookupLabelName;
import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record GotoCodeP(int left, int id) implements PrimitiveTypeCode
{
    @Override
    public String toString()
    {
        return String.format("if %s goto %s",
                lookupVariableName(left),
                lookupLabelName(id));
    }
}

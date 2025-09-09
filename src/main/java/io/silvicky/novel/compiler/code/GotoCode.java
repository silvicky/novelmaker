package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.tokens.OperatorType;

import static io.silvicky.novel.compiler.Compiler.lookupLabelName;
import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record GotoCode(int left, int right, OperatorType op, int id) implements Code
{
    @Override
    public String toString()
    {
        if(op.properties== OperatorType.OperatorArgsProperties.BINARY)return String.format("if %s %s %s goto %s",
                lookupVariableName(left),
                op.symbol,
                lookupVariableName(right),
                lookupLabelName(id));
        else return String.format("if %s %s goto %s",
                op.symbol,
                lookupVariableName(left),
                lookupLabelName(id));
    }
}

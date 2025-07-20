package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.tokens.OperatorType;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record AssignCode(int target, int left, int right, OperatorType op) implements Code
{
    public String toString()
    {
        if(op.properties== OperatorType.OperatorArgsProperties.BINARY)return lookupVariableName(target)+"="+lookupVariableName(left)+op.symbol+lookupVariableName(right);
        else return lookupVariableName(target)+"="+op.symbol+lookupVariableName(left);
    }
}

package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.tokens.OperatorType;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record AssignVariableNumberCode(int target, int left, long right, OperatorType op) implements Code
{
    public String toString()
    {
        if(op.properties== OperatorType.OperatorArgsProperties.BINARY)return lookupVariableName(target)+"="+lookupVariableName(left)+op.symbol+right;
        else return lookupVariableName(target)+"="+op.symbol+lookupVariableName(left);
    }
}
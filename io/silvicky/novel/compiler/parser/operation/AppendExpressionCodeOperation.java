package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.parser.BinaryOperator;
import io.silvicky.novel.compiler.parser.Expression;

public record AppendExpressionCodeOperation(Expression target, Expression left, Expression right, BinaryOperator operator) implements Operation
{
    @Override
    public void execute()
    {
        target.codes.add(new AssignCode(target.resultId,left.resultId,right.resultId,operator.operatorType));
    }
}

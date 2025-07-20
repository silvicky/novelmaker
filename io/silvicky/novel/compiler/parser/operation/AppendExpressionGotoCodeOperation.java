package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.code.GotoCode;
import io.silvicky.novel.compiler.parser.Expression;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.OperatorType;

public record AppendExpressionGotoCodeOperation(NonTerminal target, Expression condition, int gotoTarget,OperatorType op) implements Operation
{
    @Override
    public void execute()
    {
        target.codes.add(new GotoCode(condition.resultId,0, op,gotoTarget));
    }
}

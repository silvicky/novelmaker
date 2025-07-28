package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.parser.expression.Rotator.rotateLeft;

public class ExpressionRoot extends AbstractExpression implements ASTNode
{
    private ExpressionNew child;

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        child=new ExpressionNew();
        ret.add(child);
        return ret;
    }

    @Override
    public void travel()
    {
        if(child.right instanceof ExpressionNew)child= rotateLeft(child);
        child.travel();
        codes.addAll(child.codes);
        codes.add(new AssignCode(resultId,child.resultId,child.resultId, OperatorType.NOP));
    }
}

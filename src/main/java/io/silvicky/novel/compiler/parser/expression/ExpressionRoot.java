package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.util.Util.rotateLeft;

public class ExpressionRoot extends AbstractExpression implements ASTNode
{
    private ExpressionNew child=null;

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.SEMICOLON)return ret;
        child=new ExpressionNew();
        ret.add(child);
        return ret;
    }

    @Override
    public void travel()
    {
        if(child==null)return;
        if(child.right instanceof ExpressionNew)child= rotateLeft(child);
        child.travel();
        codes.addAll(child.codes);
        type=child.type;
        leftId=child.leftId;
        isDirect=child.isDirect;
        resultId=child.resultId;
    }
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class LogicalAndExpressionResidue extends AbstractExpressionResidue<LogicalAndExpression>
{
    private LogicalAndExpression child=null;
    protected LogicalAndExpressionResidue(LogicalAndExpression root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.AND_AND)
        {
            child = new LogicalAndExpression();
            ret.add(child);
            ret.add(new OperatorToken(OperatorType.AND_AND));
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.right=child;
    }
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class LogicalOrExpressionResidue extends AbstractExpressionResidue<LogicalOrExpression>
{
    private LogicalOrExpression child=null;
    protected LogicalOrExpressionResidue(LogicalOrExpression root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.OR_OR)
        {
            child = new LogicalOrExpression();
            ret.add(child);
            ret.add(new OperatorToken(OperatorType.OR_OR));
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.right=child;
    }
}

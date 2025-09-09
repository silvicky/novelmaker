package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class InclusiveOrExpressionResidue extends AbstractExpressionResidue<InclusiveOrExpression>
{
    private InclusiveOrExpression child=null;
    protected InclusiveOrExpressionResidue(InclusiveOrExpression root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.OR)
        {
            child = new InclusiveOrExpression();
            ret.add(child);
            ret.add(new OperatorToken(OperatorType.OR));
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.right=child;
    }
}

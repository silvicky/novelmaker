package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class ExclusiveOrExpressionResidue extends AbstractExpressionResidue<ExclusiveOrExpression>
{
    private ExclusiveOrExpression child=null;
    protected ExclusiveOrExpressionResidue(ExclusiveOrExpression root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.XOR)
        {
            child = new ExclusiveOrExpression();
            ret.add(child);
            ret.add(new OperatorToken(OperatorType.XOR));
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.right=child;
    }
}

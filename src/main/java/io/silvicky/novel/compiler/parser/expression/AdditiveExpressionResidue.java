package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class AdditiveExpressionResidue extends AbstractExpressionResidue<AdditiveExpression>
{
    private AdditiveExpression child=null;
    private OperatorType type=null;
    protected AdditiveExpressionResidue(AdditiveExpression root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&(operatorToken.type==OperatorType.PLUS||operatorToken.type==OperatorType.MINUS))
        {
            child = new AdditiveExpression();
            type=operatorToken.type;
            ret.add(child);
            ret.add(new OperatorToken(type));
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.right=child;
        root.op =type;
    }
}

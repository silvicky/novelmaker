package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class AndExpressionResidue extends AbstractExpressionResidue<AndExpression>
{
    private AndExpression child=null;
    protected AndExpressionResidue(AndExpression root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.AND)
        {
            child = new AndExpression();
            ret.add(child);
            ret.add(new OperatorToken(OperatorType.AND));
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.right=child;
    }
}

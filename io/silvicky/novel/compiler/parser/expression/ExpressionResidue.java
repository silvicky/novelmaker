package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class ExpressionResidue extends AbstractExpressionResidue<ExpressionNew>
{
    private ExpressionNew child=null;

    protected ExpressionResidue(ExpressionNew root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.COMMA)
        {
            child=new ExpressionNew();
            ret.add(child);
            ret.add(new OperatorToken(OperatorType.COMMA));
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.right=child;
    }
}

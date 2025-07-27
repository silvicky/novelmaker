package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class ConditionalExpressionResidue extends AbstractExpressionResidue<ConditionalExpression>
{
    public ExpressionNew middle=null;
    public ConditionalExpression right=null;
    protected ConditionalExpressionResidue(ConditionalExpression root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.QUESTION)
        {
            middle=new ExpressionNew();
            right=new ConditionalExpression();
            ret.add(right);
            ret.add(new OperatorToken(OperatorType.COLON));
            ret.add(middle);
            ret.add(new OperatorToken(OperatorType.QUESTION));
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.middle=middle;
        root.right=right;
    }
}

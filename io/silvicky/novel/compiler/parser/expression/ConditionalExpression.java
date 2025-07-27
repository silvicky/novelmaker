package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;

import java.util.ArrayList;
import java.util.List;

public class ConditionalExpression extends AbstractExpression
{
    public LogicalOrExpression left;
    public ExpressionNew middle=null;
    public ConditionalExpression right=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new LogicalOrExpression();
        ret.add(new ConditionalExpressionResidue(this));
        ret.add(left);
        return ret;
    }
}

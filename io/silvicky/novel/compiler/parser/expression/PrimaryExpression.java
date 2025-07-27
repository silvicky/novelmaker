package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class PrimaryExpression extends AbstractExpression
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken)
        {
            ret.add(new IdentifierToken(identifierToken.id));
            return ret;
        }
        if(next instanceof NumberToken numberToken)
        {
            ret.add(new NumberToken(numberToken.value));
            return ret;
        }
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(new ExpressionNew());
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        return ret;
    }
}

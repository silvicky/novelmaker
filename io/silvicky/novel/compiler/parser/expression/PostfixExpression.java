package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class PostfixExpression extends AbstractExpression
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken&&second instanceof OperatorToken operatorToken&&(operatorToken.type==OperatorType.PLUS_PLUS||operatorToken.type==OperatorType.MINUS_MINUS))
        {
            if(operatorToken.type== OperatorType.PLUS_PLUS)
            {
                ret.add(new OperatorToken(OperatorType.PLUS_PLUS));
                ret.add(new IdentifierToken(identifierToken.id));
                return ret;
            }
            ret.add(new OperatorToken(OperatorType.MINUS_MINUS));
            ret.add(new IdentifierToken(identifierToken.id));
            return ret;
        }
        ret.add(new PrimaryExpression());
        return ret;
    }
}

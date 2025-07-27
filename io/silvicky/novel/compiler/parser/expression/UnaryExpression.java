package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class UnaryExpression extends AbstractExpression
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken)
        {
            if(operatorToken.type==OperatorType.PLUS_PLUS)
            {
                ret.add(new IdentifierToken(((IdentifierToken)second).id));
                ret.add(new OperatorToken(OperatorType.PLUS_PLUS));
                return ret;
            }
            if(operatorToken.type==OperatorType.MINUS_MINUS)
            {
                ret.add(new IdentifierToken(((IdentifierToken)second).id));
                ret.add(new OperatorToken(OperatorType.MINUS_MINUS));
                return ret;
            }
            //TODO Unary plus&minus
            if(operatorToken.type==OperatorType.NOT)
            {
                ret.add(new UnaryExpression());
                ret.add(new OperatorToken(OperatorType.NOT));
                return ret;
            }
            if(operatorToken.type==OperatorType.REVERSE)
            {
                ret.add(new UnaryExpression());
                ret.add(new OperatorToken(OperatorType.REVERSE));
                return ret;
            }
        }
        ret.add(new PostfixExpression());
        return ret;
    }
}

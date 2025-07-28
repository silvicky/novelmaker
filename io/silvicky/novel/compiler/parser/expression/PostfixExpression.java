package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.code.AssignVariableNumberCode;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariable;

public class PostfixExpression extends AbstractExpression
{
    private OperatorType op=null;
    private int target=-1;
    private PrimaryExpression nextExpression=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken&&second instanceof OperatorToken operatorToken&&(operatorToken.type==OperatorType.PLUS_PLUS||operatorToken.type==OperatorType.MINUS_MINUS))
        {
            op=operatorToken.type;
            target=lookupVariable(identifierToken.id);
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
        nextExpression=new PrimaryExpression();
        ret.add(nextExpression);
        return ret;
    }

    @Override
    public void travel()
    {
        if(nextExpression==null)
        {
            codes.add(new AssignCode(resultId,target,-1,OperatorType.NOP));
            codes.add(new AssignVariableNumberCode(target,target,1,op.baseType));
        }
        else
        {
            nextExpression.travel();
            codes.add(new AssignCode(resultId,nextExpression.resultId,-1,OperatorType.NOP));
        }
    }
}

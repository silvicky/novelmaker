package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.code.AssignVariableNumberCode;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariable;
import static io.silvicky.novel.compiler.parser.expression.Rotator.rotateLeft;

public class PrimaryExpression extends AbstractExpression
{
    private long numericVal;
    private int variable=-1;
    private ExpressionNew nextExpression=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken)
        {
            variable=lookupVariable(identifierToken.id);
            ret.add(new IdentifierToken(identifierToken.id));
            return ret;
        }
        if(next instanceof NumberToken numberToken)
        {
            numericVal=numberToken.value;
            ret.add(new NumberToken(numberToken.value));
            return ret;
        }
        nextExpression=new ExpressionNew();
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(nextExpression);
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        return ret;
    }

    @Override
    public void travel()
    {
        if(nextExpression!=null)
        {
            if(nextExpression.right instanceof ExpressionNew)nextExpression= rotateLeft(nextExpression);
            nextExpression.travel();
            codes.addAll(nextExpression.codes);
            codes.add(new AssignCode(resultId,nextExpression.resultId,-1,OperatorType.NOP));
        }
        else if(variable!=-1)
        {
            codes.add(new AssignCode(resultId,variable,-1,OperatorType.NOP));
        }
        else
        {
            codes.add(new AssignVariableNumberCode(resultId,-1,numericVal,OperatorType.COMMA));
        }
    }
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.code.AssignVariableNumberCode;
import io.silvicky.novel.compiler.code.CallCode;
import io.silvicky.novel.compiler.code.FetchReturnValueCode;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupLabel;
import static io.silvicky.novel.compiler.Compiler.lookupVariable;
import static io.silvicky.novel.compiler.parser.expression.Rotator.rotateLeft;

public class PrimaryExpression extends AbstractExpression
{
    private long numericVal;
    private int variable=-1;
    private ExpressionNew nextExpression=null;
    private int callTarget;
    private Parameters parameters=null;
    public final List<Integer> parameterAddresses=new ArrayList<>();
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken)
        {
            if(second instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.L_PARENTHESES)
            {
                parameters=new Parameters(this);
                callTarget=lookupLabel(identifierToken.id);
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(parameters);
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                ret.add(new IdentifierToken(identifierToken.id));
                return ret;
            }
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
        if(parameters!=null)
        {
            parameters.travel();
            codes.addAll(parameters.codes);
            codes.add(new CallCode(callTarget,parameterAddresses));
            codes.add(new FetchReturnValueCode(resultId));
        }
        else if(nextExpression!=null)
        {
            if(nextExpression.right instanceof ExpressionNew)nextExpression= rotateLeft(nextExpression);
            nextExpression.travel();
            codes.addAll(nextExpression.codes);
            codes.add(new AssignCode(resultId,nextExpression.resultId,nextExpression.resultId,OperatorType.NOP));
        }
        else if(variable!=-1)
        {
            codes.add(new AssignCode(resultId,variable,variable,OperatorType.NOP));
        }
        else
        {
            codes.add(new AssignVariableNumberCode(resultId,0,numericVal,OperatorType.COMMA));
        }
    }
}

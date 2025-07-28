package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.code.AssignVariableNumberCode;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariable;

public class UnaryExpression extends AbstractExpression
{
    private OperatorType op=null;
    private int target=-1;
    private UnaryExpression child=null;
    private PostfixExpression nextExpression=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken)
        {
            if(operatorToken.type==OperatorType.PLUS_PLUS)
            {
                op=OperatorType.PLUS_PLUS;
                target=lookupVariable(((IdentifierToken)second).id);
                ret.add(new IdentifierToken(((IdentifierToken)second).id));
                ret.add(new OperatorToken(op));
                return ret;
            }
            if(operatorToken.type==OperatorType.MINUS_MINUS)
            {
                op=OperatorType.MINUS_MINUS;
                target=lookupVariable(((IdentifierToken)second).id);
                ret.add(new IdentifierToken(((IdentifierToken)second).id));
                ret.add(new OperatorToken(op));
                return ret;
            }
            if(operatorToken.type==OperatorType.PLUS)
            {
                op=OperatorType.PLUS;
                child=new UnaryExpression();
                ret.add(child);
                ret.add(new OperatorToken(op));
                return ret;
            }
            if(operatorToken.type==OperatorType.MINUS)
            {
                op=OperatorType.MINUS;
                child=new UnaryExpression();
                ret.add(child);
                ret.add(new OperatorToken(op));
                return ret;
            }
            if(operatorToken.type==OperatorType.NOT)
            {
                op=OperatorType.NOT;
                child=new UnaryExpression();
                ret.add(child);
                ret.add(new OperatorToken(op));
                return ret;
            }
            if(operatorToken.type==OperatorType.REVERSE)
            {
                op=OperatorType.REVERSE;
                child=new UnaryExpression();
                ret.add(child);
                ret.add(new OperatorToken(op));
                return ret;
            }
        }
        nextExpression=new PostfixExpression();
        ret.add(nextExpression);
        return ret;
    }

    @Override
    public void travel()
    {
        if(nextExpression==null)
        {
            if(target!=-1)
            {
                codes.add(new AssignVariableNumberCode(target,target,1,op.baseType));
                codes.add(new AssignCode(resultId,target,target,OperatorType.NOP));
            }
            else
            {
                child.travel();
                codes.addAll(child.codes);
                codes.add(new AssignVariableNumberCode(resultId,child.resultId,0,op));
            }
        }
        else
        {
            nextExpression.travel();
            codes.addAll(nextExpression.codes);
            codes.add(new AssignCode(resultId,nextExpression.resultId,nextExpression.resultId,OperatorType.NOP));
        }
    }
}

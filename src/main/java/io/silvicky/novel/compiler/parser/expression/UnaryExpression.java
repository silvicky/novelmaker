package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.*;
import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.code.raw.AssignVariableNumberCode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.AbstractPointer;
import io.silvicky.novel.compiler.types.PointerType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.compiler.types.VoidType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.util.Util.getResultType;

public class UnaryExpression extends AbstractExpression
{
    private OperatorType op=null;
    private UnaryExpression child=null;
    private PostfixExpression nextExpression=null;
    private CastExpression castExpression=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        //TODO sizeof
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken)
        {
            if(operatorToken.type==OperatorType.PLUS_PLUS
            ||operatorToken.type==OperatorType.MINUS_MINUS
            )
            {
                op=operatorToken.type;
                child=new UnaryExpression();
                ret.add(child);
                ret.add(new OperatorToken(op));
                return ret;
            }
            else if(operatorToken.type==OperatorType.NOT
                    ||operatorToken.type==OperatorType.REVERSE
                    ||operatorToken.type==OperatorType.MULTIPLY
                    ||operatorToken.type==OperatorType.AND
                    ||operatorToken.type==OperatorType.MINUS
                    ||operatorToken.type==OperatorType.PLUS)
            {
                op=operatorToken.type;
                castExpression=new CastExpression();
                ret.add(castExpression);
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
        if (nextExpression != null)
        {
            nextExpression.travel();
            type= nextExpression.type;
            leftId=nextExpression.leftId;
            isDirect= nextExpression.isDirect;
            codes.addAll(nextExpression.codes);
            resultId= nextExpression.resultId;
        }
        else if(child!=null)
        {
            resultId=requestInternalVariable();
            child.travel();
            type= child.type;
            if(child.leftId==-1)throw new GrammarException("not lvalue");
            leftId=-1;
            codes.addAll(child.codes);
            int realLeft;
            if(child.isDirect)realLeft=child.leftId;
            else
            {
                realLeft=requestInternalVariable();
                codes.add(new DereferenceCode(realLeft,leftId,new PointerType(new PointerType(new VoidType()))));
            }
            codes.add(new AssignVariableNumberCode(realLeft,realLeft,1,type,type,type,op.baseType));
            codes.add(new AssignCode(resultId,realLeft,realLeft,type,type,type,OperatorType.NOP));

        }
        else
        {
            resultId=requestInternalVariable();
            castExpression.travel();
            codes.addAll(castExpression.codes);
            if(op==OperatorType.MULTIPLY)
            {
                if(!(castExpression.type instanceof AbstractPointer abstractPointer))throw new GrammarException("not a pointer/array");
                leftId=castExpression.resultId;
                isDirect=false;
                type= abstractPointer.baseType();
                codes.add(new DereferenceCode(resultId,leftId, castExpression.type));
            }
            else if(op==OperatorType.AND)
            {
                if(castExpression.leftId==-1)throw new GrammarException("not lvalue");
                type=new PointerType(castExpression.type);
                if(castExpression.isDirect)
                {
                    codes.add(new ReferenceCode(resultId,castExpression.leftId));
                }
                else
                {
                    codes.add(new AssignCode(resultId,castExpression.leftId,castExpression.leftId,type,PrimitiveType.INT, PrimitiveType.INT,OperatorType.NOP));
                }
                leftId=-1;
            }
            else
            {
                leftId=-1;
                type=getResultType(castExpression.type, castExpression.type, op);
                codes.add(new AssignCode(resultId,castExpression.resultId,castExpression.resultId,type, castExpression.type, castExpression.type, op));
            }
        }
    }
}

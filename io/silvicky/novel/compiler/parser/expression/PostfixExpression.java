package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.code.AssignVariableNumberCode;
import io.silvicky.novel.compiler.code.DereferenceCode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.AbstractPointer;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.util.Util.getResultType;

public class PostfixExpression extends AbstractExpression
{
    private PrimaryExpression nextExpression=null;
    public final List<Postfix> postfixes=new ArrayList<>();
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        nextExpression=new PrimaryExpression();
        ret.add(new Postfixes(this));
        ret.add(nextExpression);
        return ret;
    }

    @Override
    public void travel()
    {
        //TODO maybe rewrite?
        int curResult=nextExpression.resultId,nextResult;
        leftId= nextExpression.leftId;
        isDirect= nextExpression.isDirect;
        type= nextExpression.type;
        for(Postfix postfix:postfixes)
        {
            switch (postfix.operatorType)
            {
                case PLUS_PLUS,MINUS_MINUS:
                {
                    if(leftId==-1)throw new GrammarException("not lvalue");
                    int realLeft;
                    if(isDirect)realLeft=leftId;
                    else
                    {
                        realLeft=requestInternalVariable();
                        codes.add(new DereferenceCode(realLeft,leftId,type));
                    }
                    nextResult=requestInternalVariable();
                    codes.add(new AssignCode(nextResult,realLeft,realLeft,type,type,type,OperatorType.NOP));
                    codes.add(new AssignVariableNumberCode(realLeft,realLeft,1,type,type,type,postfix.operatorType.baseType));
                    curResult=nextResult;
                    leftId=-1;
                }
                case L_BRACKET:
                {
                    int tmp1=requestInternalVariable();
                    Type resultType=getResultType(type,postfix.nextExpression.type,OperatorType.PLUS);
                    if(!(resultType instanceof AbstractPointer abstractPointer))throw new GrammarException("not a pointer/array");
                    codes.add(new AssignCode(tmp1,curResult,postfix.nextExpression.resultId,resultType,type,postfix.nextExpression.type, OperatorType.PLUS));
                    int tmp2=requestInternalVariable();
                    codes.add(new DereferenceCode(tmp2,tmp1,abstractPointer.baseType()));
                    curResult=tmp2;
                    leftId=tmp1;
                    isDirect=false;
                    type= abstractPointer.baseType();
                }
                case L_PARENTHESES:
                {
                    //TODO Function Call
                }
            }
        }
        codes.add(new AssignCode(resultId,curResult,curResult,type,type,type,OperatorType.NOP));
    }
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.raw.CallCode;
import io.silvicky.novel.compiler.code.raw.DereferenceCode;
import io.silvicky.novel.compiler.code.raw.FetchReturnValueCode;
import io.silvicky.novel.compiler.code.raw.IndirectAssignCode;
import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.code.raw.AssignVariableNumberCode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.*;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.util.Util.getResultType;

public class PostfixExpression extends AbstractExpression
{
    private PrimaryExpression nextExpression;
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
        nextExpression.travel();
        codes.addAll(nextExpression.codes);
        int curResult=nextExpression.resultId,nextResult;
        leftId= nextExpression.leftId;
        isDirect= nextExpression.isDirect;
        type= nextExpression.type;
        for(Postfix postfix:postfixes)
        {
            postfix.travel();
            codes.addAll(postfix.codes);
            switch (postfix.operatorType)
            {
                case PLUS_PLUS,MINUS_MINUS->
                {
                    if(leftId==-1)throw new GrammarException("not lvalue");
                    if(type instanceof ConstType||type instanceof ArrayType||type instanceof FunctionType)throw new GrammarException("modifying const value");
                    nextResult=requestInternalVariable(type);
                    if(isDirect)
                    {
                        codes.add(new AssignCode(nextResult,leftId,0,type,type,type,OperatorType.NOP));
                        codes.add(new AssignVariableNumberCode(leftId,leftId,1,type,type,PrimitiveType.INT,postfix.operatorType.baseType));
                    }
                    else
                    {
                        codes.add(new DereferenceCode(nextResult,leftId,new PointerType(type)));
                        int calResult=requestInternalVariable(type);
                        codes.add(new AssignVariableNumberCode(calResult,nextResult,1,type,type,PrimitiveType.INT,postfix.operatorType.baseType));
                        codes.add(new IndirectAssignCode(leftId,calResult,type));
                    }
                    curResult=nextResult;
                    leftId=-1;
                }
                case L_BRACKET->
                {
                    Type resultType=getResultType(type,postfix.nextExpression.type,OperatorType.PLUS);
                    if(!(resultType instanceof AbstractPointer abstractPointer))throw new GrammarException("not a pointer/array");
                    int tmp1=requestInternalVariable(resultType);
                    codes.add(new AssignCode(tmp1,curResult,postfix.nextExpression.resultId,resultType,type,postfix.nextExpression.type, OperatorType.PLUS));
                    type= abstractPointer.baseType();
                    int tmp2=requestInternalVariable(type);
                    if(abstractPointer.baseType() instanceof ArrayType) codes.add(new AssignCode(tmp2,tmp1,tmp1,type,type,type,OperatorType.NOP));
                    else codes.add(new DereferenceCode(tmp2,tmp1,resultType));
                    curResult=tmp2;
                    leftId=tmp1;
                    isDirect=false;

                }
                case L_PARENTHESES->
                {
                    int tmp=curResult,tmp2;
                    while(type instanceof PointerType pointerType)
                    {
                        type=pointerType.baseType();
                        tmp2=requestInternalVariable(type);
                        codes.add(new DereferenceCode(tmp2,tmp,pointerType));
                        tmp=tmp2;
                    }
                    if(!(type instanceof FunctionType functionType))throw new GrammarException("not a function");
                    int paramsSize=functionType.params().size();
                    int argsSize=postfix.arguments.size();
                    boolean isVariadic=(!functionType.params().isEmpty())&&functionType.params().get(paramsSize-1)==PrimitiveType.ELLIPSIS;
                    if(isVariadic)paramsSize--;
                    if(isVariadic?(argsSize<paramsSize):(argsSize!=paramsSize))throw new GrammarException("args mismatch");
                    List<Integer> castArguments=new ArrayList<>();
                    List<Type> castTypes=new ArrayList<>();
                    for(int i = 0; i<postfix.arguments.size(); i++)
                    {
                        Type aType=postfix.arguments.get(i).first();
                        if(i>=paramsSize||aType.equals(functionType.params().get(i)))
                        {
                            castArguments.add(postfix.arguments.get(i).second());
                            castTypes.add(aType);
                            continue;
                        }
                        Type pType=functionType.params().get(i);
                        int id=requestInternalVariable(pType);
                        codes.add(new AssignCode(id,postfix.arguments.get(i).second(),0,pType,aType,aType,OperatorType.NOP));
                        castArguments.add(id);
                        castTypes.add(pType);
                    }
                    codes.add(new CallCode(tmp,castArguments,castTypes));
                    type=functionType.returnType();
                    if(type==PrimitiveType.VOID)
                    {
                        curResult=-1;
                        leftId=-1;
                        isDirect=false;
                        continue;
                    }
                    curResult=requestInternalVariable(type);
                    codes.add(new FetchReturnValueCode(curResult,type));
                    leftId=-1;
                    isDirect=false;
                }
            }
        }
        resultId=curResult;
    }

    @Override
    public Pair<PrimitiveType, Object> evaluateConstExpr()
    {
        if(!postfixes.isEmpty())throw new GrammarException("postfix forbidden in constexpr");
        return nextExpression.evaluateConstExpr();
    }
}

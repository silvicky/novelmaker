package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.raw.DereferenceCode;
import io.silvicky.novel.compiler.code.raw.IndirectAssignCode;
import io.silvicky.novel.compiler.code.ReferenceCode;
import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.code.raw.AssignVariableNumberCode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.declaration.BaseTypeBuilderRoot;
import io.silvicky.novel.compiler.parser.declaration.UnaryDeclaration;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.*;

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
    public BaseTypeBuilderRoot baseTypeBuilderRoot=null;
    public UnaryDeclaration unaryDeclaration=null;
    public UnaryExpression sizeofExpression=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
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
        else if(next instanceof KeywordToken keywordToken&&keywordToken.type== KeywordType.SIZEOF)
        {
            SizeofResidue sizeofResidue=new SizeofResidue(this);
            ret.add(new ResolveOperation(sizeofResidue));
            ret.add(sizeofResidue);
            ret.add(new KeywordToken(KeywordType.SIZEOF));
            return ret;
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
        else if(sizeofExpression!=null)
        {
            sizeofExpression.travel();
            type=Type.ADDRESS_TYPE;
            resultId=requestInternalVariable(type);
            codes.add(new AssignVariableNumberCode(resultId,0,sizeofExpression.type.getSize(),type,type,type,OperatorType.COMMA));
        }
        else if(baseTypeBuilderRoot!=null)
        {
            baseTypeBuilderRoot.travel();
            unaryDeclaration.receivedType=baseTypeBuilderRoot.type;
            unaryDeclaration.travel();
            type=Type.ADDRESS_TYPE;
            resultId=requestInternalVariable(type);
            codes.add(new AssignVariableNumberCode(resultId,0,unaryDeclaration.type.getSize(),type,type,type,OperatorType.COMMA));
        }
        else if(child!=null)
        {
            child.travel();
            type= child.type;
            if(type instanceof ConstType ||type instanceof ArrayType ||type instanceof FunctionType)throw new GrammarException("modifying const value");
            resultId=requestInternalVariable(type);
            if(child.leftId==-1)throw new GrammarException("not lvalue");
            leftId=-1;
            codes.addAll(child.codes);
            if(child.isDirect)
            {
                codes.add(new AssignVariableNumberCode(child.leftId,child.leftId,1,type,type,PrimitiveType.INT,op.baseType));
                codes.add(new AssignCode(resultId,child.leftId,0,type,type,type,OperatorType.NOP));
            }
            else
            {
                int realValue=requestInternalVariable(type);
                codes.add(new DereferenceCode(realValue,child.leftId,new PointerType(type)));
                codes.add(new AssignVariableNumberCode(resultId,realValue,1,type,type,PrimitiveType.INT,op.baseType));
                codes.add(new IndirectAssignCode(child.leftId,resultId,type));
            }
        }
        else
        {
            castExpression.travel();
            codes.addAll(castExpression.codes);
            if(op==OperatorType.MULTIPLY)
            {
                //TODO Array is not real pointer, how to convert?
                if(!(castExpression.type instanceof AbstractPointer abstractPointer))throw new GrammarException("not a pointer/array");
                leftId=castExpression.resultId;
                isDirect=false;
                type= abstractPointer.baseType();
                resultId=requestInternalVariable(type);
                if(abstractPointer.baseType() instanceof ArrayType) codes.add(new AssignCode(resultId,leftId,leftId,type,type,type,OperatorType.NOP));
                else codes.add(new DereferenceCode(resultId,leftId, castExpression.type));
            }
            else if(op==OperatorType.AND)
            {
                if(castExpression.leftId==-1)throw new GrammarException("not lvalue");
                type=new PointerType(castExpression.type);
                resultId=requestInternalVariable(type);
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
                resultId=requestInternalVariable(type);
                codes.add(new AssignCode(resultId,castExpression.resultId,castExpression.resultId,type, castExpression.type, castExpression.type, op));
            }
        }
    }
}

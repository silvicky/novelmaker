package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.code.raw.AssignNumberCode;
import io.silvicky.novel.compiler.code.ReferenceCode;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.ArrayType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariable;
import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.util.Util.rotateLeft;

public class PrimaryExpression extends AbstractExpression
{
    private Object numericVal;
    private ExpressionNew nextExpression=null;
    private String variableName=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken)
        {
            variableName=identifierToken.id;
            ret.add(new IdentifierToken(identifierToken.id));
            return ret;
        }
        if(next instanceof NumberToken<?> numberToken)
        {
            numericVal=numberToken.value;
            type=numberToken.type;
            ret.add(new NumberToken<>());
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
            type=nextExpression.type;
            leftId=nextExpression.leftId;
            resultId=nextExpression.resultId;
        }
        else if(variableName!=null)
        {
            resultId=requestInternalVariable();
            type= lookupVariable(variableName).second();
            leftId=lookupVariable(variableName).first();
            isDirect=true;
            if(type instanceof ArrayType)codes.add(new ReferenceCode(resultId,leftId));
            else codes.add(new AssignCode(resultId,leftId,leftId,type,type,type,OperatorType.NOP));
        }
        else
        {
            resultId=requestInternalVariable();
            codes.add(new AssignNumberCode(resultId,numericVal,type,type));
        }
    }
}

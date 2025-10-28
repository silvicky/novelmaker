package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.Preprocessor;
import io.silvicky.novel.compiler.code.LeaCode;
import io.silvicky.novel.compiler.code.raw.AssignNumberCode;
import io.silvicky.novel.compiler.code.raw.AssignUnaryCode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.ArrayType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            Pair<Integer, Type> pr= Objects.requireNonNull(lookupVariable(variableName));
            type= pr.second();
            resultId=requestInternalVariable(type);
            leftId=pr.first();
            isDirect=true;
            if(type instanceof ArrayType)codes.add(new LeaCode(resultId,leftId,0));
            else codes.add(new AssignUnaryCode(resultId,leftId,type,type,OperatorType.NOP));
        }
        else
        {
            resultId=requestInternalVariable(type);
            codes.add(new AssignNumberCode(resultId,numericVal,type,type));
        }
    }

    @Override
    public Pair<PrimitiveType, Object> evaluateConstExpr()
    {
        if(nextExpression!=null)
        {
            if(nextExpression.right instanceof ExpressionNew)nextExpression= rotateLeft(nextExpression);
            return nextExpression.evaluateConstExpr();
        }
        else if(variableName!=null)
        {
            //TODO const value
            if(Preprocessor.isPreprocessing)return new Pair<>(PrimitiveType.BOOL,false);
            throw new GrammarException("not a constant value");
        }
        else
        {
            return new Pair<>((PrimitiveType) type,numericVal);
        }
    }
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.parser.TypeBuilder;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;

public class CastExpression extends AbstractExpression
{
    public Type castType;
    private TypeBuilder typeBuilder;
    private CastExpression child=null;
    private UnaryExpression nextExpression=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        //TODO
        List<AbstractToken> ret=new ArrayList<>();
        if (next instanceof OperatorToken operatorToken && operatorToken.type == OperatorType.L_PARENTHESES && second instanceof KeywordToken keywordToken
                && (keywordToken.type== KeywordType.UNSIGNED
                    ||keywordToken.type==KeywordType.INT
                    ||keywordToken.type==KeywordType.FLOAT
                    ||keywordToken.type==KeywordType.BOOL
                    ||keywordToken.type==KeywordType.DOUBLE
                    ||keywordToken.type==KeywordType.VOID
                    ||keywordToken.type==KeywordType.CONST
                    ||keywordToken.type==KeywordType.SHORT
                    ||keywordToken.type==KeywordType.LONG
                    ||keywordToken.type==KeywordType.CHAR
                    ||keywordToken.type==KeywordType.SIGNED))
        {
            child=new CastExpression();
            //TODO this is wrong
            typeBuilder=new TypeBuilder();
            ret.add(child);
            ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
            ret.add(typeBuilder);
            ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        }
        else
        {
            nextExpression=new UnaryExpression();
            ret.add(nextExpression);
        }
        return ret;
    }
    @Override
    public void travel()
    {
        resultId=requestInternalVariable();
        nextExpression.travel();
        type= nextExpression.type;
        leftId= nextExpression.leftId;
        isDirect= nextExpression.isDirect;
        codes.addAll(nextExpression.codes);
        codes.add(new AssignCode(resultId, nextExpression.resultId, 0,type,type,type,OperatorType.NOP));
        //TODO
    }
}

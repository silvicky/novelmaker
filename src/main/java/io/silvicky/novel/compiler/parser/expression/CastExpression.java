package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.parser.declaration.BaseTypeBuilderRoot;
import io.silvicky.novel.compiler.parser.declaration.UnaryDeclaration;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;

public class CastExpression extends AbstractExpression
{
    private BaseTypeBuilderRoot baseTypeBuilderRoot;
    private UnaryDeclaration unaryDeclaration;
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
            unaryDeclaration=new UnaryDeclaration();
            baseTypeBuilderRoot=new BaseTypeBuilderRoot();
            ret.add(child);
            ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
            ret.add(unaryDeclaration);
            ret.add(baseTypeBuilderRoot);
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
        if(nextExpression!=null)
        {
            nextExpression.travel();
            type = nextExpression.type;
            leftId = nextExpression.leftId;
            isDirect = nextExpression.isDirect;
            codes.addAll(nextExpression.codes);
            resultId=nextExpression.resultId;
            return;
        }
        baseTypeBuilderRoot.travel();
        unaryDeclaration.receivedType= baseTypeBuilderRoot.type;
        unaryDeclaration.travel();
        type= unaryDeclaration.type;
        resultId=requestInternalVariable(type);
        leftId=-1;
        isDirect=false;
        child.travel();
        codes.addAll(child.codes);
        codes.add(new AssignCode(resultId,child.resultId,0,type,child.type,child.type,OperatorType.NOP));
    }
}

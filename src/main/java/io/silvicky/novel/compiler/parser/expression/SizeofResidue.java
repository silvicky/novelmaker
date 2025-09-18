package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.declaration.BaseTypeBuilderRoot;
import io.silvicky.novel.compiler.parser.declaration.UnaryDeclaration;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class SizeofResidue extends AbstractExpressionResidue<UnaryExpression>
{
    private BaseTypeBuilderRoot baseTypeBuilderRoot=null;
    private UnaryDeclaration unaryDeclaration=null;
    private UnaryExpression sizeofExpression=null;
    protected SizeofResidue(UnaryExpression root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
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
            unaryDeclaration=new UnaryDeclaration();
            baseTypeBuilderRoot=new BaseTypeBuilderRoot();
            ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
            ret.add(unaryDeclaration);
            ret.add(baseTypeBuilderRoot);
            ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        }
        else
        {
            sizeofExpression=new UnaryExpression();
            ret.add(sizeofExpression);
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.baseTypeBuilderRoot=baseTypeBuilderRoot;
        root.unaryDeclaration=unaryDeclaration;
        root.sizeofExpression=sizeofExpression;
    }
}

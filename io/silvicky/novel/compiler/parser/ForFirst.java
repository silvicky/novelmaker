package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ForFirst extends NonTerminal
{
    public final NonTerminal directParent;

    public ForFirst(NonTerminal directParent)
    {
        this.directParent = directParent;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof KeywordToken keywordToken&&keywordToken.type== KeywordType.INT)
        {
            VariableDeclaration declaration=new VariableDeclaration(Objects.requireNonNullElse(this.directParent, this));
            ret.add(new AppendCodeSeqOperation(this,declaration));
            ret.add(declaration);
            ret.add(new KeywordToken(KeywordType.INT));
            return ret;
        }
        else
        {
            ExpressionRoot expressionRoot=new ExpressionRoot();
            ret.add(new AppendCodeSeqOperation(this,expressionRoot));
            ret.add(expressionRoot);
            return ret;
        }
    }
}

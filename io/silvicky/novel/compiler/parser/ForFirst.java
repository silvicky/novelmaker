package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.declaration.DeclarationRoot;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.KeywordToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

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
        if(next instanceof KeywordToken)
        {
            DeclarationRoot declaration=new DeclarationRoot(Objects.requireNonNullElse(this.directParent, this),false);
            ret.add(new AppendCodeSeqOperation(this,declaration));
            ret.add(declaration);
        }
        else
        {
            ExpressionRoot expressionRoot=new ExpressionRoot();
            ret.add(new AppendCodeSeqOperation(this,expressionRoot));
            ret.add(new OperatorToken(OperatorType.SEMICOLON));
            ret.add(expressionRoot);
        }
        return ret;
    }
}

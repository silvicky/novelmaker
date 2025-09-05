package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.declaration.DeclarationRoot;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.KeywordToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ForFirst extends NonTerminal implements ASTNode
{
    public final NonTerminal directParent;
    private DeclarationRoot declaration=null;
    private ExpressionRoot expressionRoot=null;
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
            declaration=new DeclarationRoot(Objects.requireNonNullElse(this.directParent, this));
            ret.add(declaration);
        }
        else
        {
            expressionRoot=new ExpressionRoot();
            ret.add(new OperatorToken(OperatorType.SEMICOLON));
            ret.add(expressionRoot);
        }
        return ret;
    }

    @Override
    public void travel()
    {
        if(declaration!=null)
        {
            declaration.travel();
            codes.addAll(declaration.codes);
        }
        if(expressionRoot!=null)
        {
            expressionRoot.travel();
            codes.addAll(expressionRoot.codes);
        }
    }
}

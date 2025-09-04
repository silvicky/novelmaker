package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class DeclarationPostfixes extends NonTerminal
{
    private final PostfixDeclaration root;

    public DeclarationPostfixes(PostfixDeclaration root)
    {
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&(operatorToken.type==OperatorType.L_PARENTHESES||operatorToken.type==OperatorType.L_BRACKET))
        {
            ret.add(new DeclarationPostfixes(root));
            DeclarationPostfix postfix=new DeclarationPostfix();
            root.postfixes.add(postfix);
            ret.add(postfix);
        }
        return ret;
    }
}


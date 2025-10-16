package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class Postfixes extends NonTerminal
{
    private final PostfixExpression root;

    public Postfixes(PostfixExpression root)
    {
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&(
                operatorToken.type==OperatorType.PLUS_PLUS
                        ||operatorToken.type==OperatorType.MINUS_MINUS
                        ||operatorToken.type==OperatorType.L_PARENTHESES
                        ||operatorToken.type==OperatorType.L_BRACKET
                        ||operatorToken.type==OperatorType.DOT
                        ||operatorToken.type==OperatorType.INDIRECT_ACCESS))
        {
            ret.add(new Postfixes(root));
            Postfix postfix=new Postfix();
            root.postfixes.add(postfix);
            ret.add(postfix);
        }
        return ret;
    }
}

package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.expression.CastExpression;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.List;

public class TypeBuilder extends NonTerminal
{
    private final CastExpression root;

    public TypeBuilder(CastExpression root)
    {
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        return null;
    }
}

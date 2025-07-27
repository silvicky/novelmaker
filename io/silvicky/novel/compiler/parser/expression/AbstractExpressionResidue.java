package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.NonTerminal;

public abstract class AbstractExpressionResidue<T> extends NonTerminal
{
    protected final T root;

    protected AbstractExpressionResidue(T root)
    {
        this.root = root;
    }

    public abstract void resolve();
}

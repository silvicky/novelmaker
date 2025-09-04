package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.NonTerminal;

public abstract class AbstractDeclaration extends NonTerminal
{
    private final boolean isAbstract;

    protected AbstractDeclaration(boolean isAbstract)
    {
        this.isAbstract = isAbstract;
    }
}

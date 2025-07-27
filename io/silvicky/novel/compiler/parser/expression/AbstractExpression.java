package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.NonTerminal;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;

public abstract class AbstractExpression extends NonTerminal
{
    public final int resultId;
    public AbstractExpression(){this.resultId=requestInternalVariable();}
}

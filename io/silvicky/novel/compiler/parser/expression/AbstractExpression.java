package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.types.Type;

public abstract class AbstractExpression extends NonTerminal implements ASTNode
{
    public int resultId;
    public Type type=null;
    public int leftId=-1;
    public boolean isDirect=false;
}

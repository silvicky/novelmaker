package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.types.Type;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;

public abstract class AbstractExpression extends NonTerminal implements ASTNode
{
    public final int resultId;
    public Type type=null;
    public int leftId=-1;
    public boolean isDirect=false;
    public AbstractExpression(){this.resultId=requestInternalVariable();}
}

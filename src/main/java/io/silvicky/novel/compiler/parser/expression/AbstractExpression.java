package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

public abstract class AbstractExpression extends NonTerminal implements ASTNode
{
    public int resultId;
    public Type type=null;
    public int leftId=-1;
    public boolean isDirect=false;
    public abstract Pair<PrimitiveType,Object> evaluateConstExpr();
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;

public class ExpressionRoot extends AbstractExpression
{
    private final boolean isDeclaration;
    public ExpressionRoot(boolean isDeclaration)
    {
        this.isDeclaration = isDeclaration;
    }
    public ExpressionRoot(){this(false);}

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new ExpressionNew());
        return ret;
    }
}

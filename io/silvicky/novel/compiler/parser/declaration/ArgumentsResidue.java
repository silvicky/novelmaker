package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.expression.AbstractExpressionResidue;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class ArgumentsResidue extends AbstractExpressionResidue<Arguments>
{
    private Arguments child;
    private final DeclarationPostfix postfix;
    protected ArgumentsResidue(Arguments root, DeclarationPostfix postfix)
    {
        super(root);
        this.postfix=postfix;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.R_PARENTHESES)return ret;
        child=new Arguments(postfix);
        ret.add(child);
        ret.add(new OperatorToken(OperatorType.COMMA));
        return ret;
    }
    @Override
    public void resolve()
    {
        root.nextExpression=child;
    }
}

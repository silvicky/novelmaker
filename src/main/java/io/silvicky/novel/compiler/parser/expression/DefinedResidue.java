package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class DefinedResidue extends AbstractExpressionResidue<UnaryExpression>
{
    private String id;
    protected DefinedResidue(UnaryExpression root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.L_PARENTHESES)
        {
            if(!(second instanceof IdentifierToken identifierToken))throw new GrammarException("not identifier");
            id=identifierToken.id;
            ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
            ret.add(new IdentifierToken(id));
            ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
            return ret;
        }
        if(next instanceof IdentifierToken identifierToken)
        {
            id=identifierToken.id;
            ret.add(new IdentifierToken(id));
            return ret;
        }
        throw new GrammarException("not identifier");
    }

    @Override
    public void resolve()
    {
        root.id=id;
    }
}

package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

public class FunctionDeclaration extends NonTerminal
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        if(!(next instanceof IdentifierToken))throw new GrammarException(this.getClass().getSimpleName()+next+second);
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new OperatorToken(OperatorType.R_BRACE));
        ret.add(new Block());
        ret.add(new OperatorToken(OperatorType.L_BRACE));
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(new Arguments());
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        ret.add(new IdentifierToken(((IdentifierToken) next).id));
        return ret;
    }
}

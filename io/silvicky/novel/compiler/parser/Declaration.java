package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

public class Declaration extends NonTerminal
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(!(next instanceof IdentifierToken))
        {
            throw new GrammarException(this.getClass().getSimpleName()+next);
        }
        if(second instanceof OperatorToken&&(((OperatorToken) second).type== OperatorType.COMMA||((OperatorToken) second).type==OperatorType.SEMICOLON))
        {
            ret.add(new VariableDeclaration(null));
            return ret;
        }
        if(second instanceof OperatorToken&&((OperatorToken) second).type==OperatorType.L_PARENTHESES)
        {
            ret.add(new FunctionDeclaration());
            return ret;
        }
        throw new GrammarException(this.getClass().getSimpleName()+next+second);
    }
}

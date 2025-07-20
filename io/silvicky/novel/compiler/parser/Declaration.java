package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class Declaration extends NonTerminal
{
    @Override
    public List<Token> lookup(Token next, Token second) throws GrammarException
    {
        List<Token> ret=new ArrayList<>();
        if(!(next instanceof IdentifierToken))
        {
            throw new GrammarException(this.getClass().getSimpleName()+next);
        }
        if(second instanceof OperatorToken&&(((OperatorToken) second).type()== OperatorType.COMMA||((OperatorToken) second).type()==OperatorType.SEMICOLON))
        {
            ret.add(new VariableDeclaration());
            return ret;
        }
        if(second instanceof OperatorToken&&((OperatorToken) second).type()==OperatorType.L_PARENTHESES)
        {
            ret.add(new FunctionDeclaration());
            return ret;
        }
        throw new GrammarException(this.getClass().getSimpleName()+next+second);
    }
}

package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.Token;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.registerVariable;

public class VariableDeclaration extends NonTerminal
{
    @Override
    public List<Token> lookup(Token next, Token second)
    {
        List<Token> ret=new ArrayList<>();
        if(!(next instanceof IdentifierToken))
        {
            throw new GrammarException(this.getClass().getSimpleName()+next);
        }
        if(!(second instanceof OperatorToken))
        {
            throw new GrammarException(this.getClass().getSimpleName()+next+second);
        }
        if(((OperatorToken) second).type() == OperatorType.COMMA)
        {

            ret.add(new VariableDeclaration());
            ret.add(new OperatorToken(OperatorType.COMMA));
            String id=((IdentifierToken) next).id();
            registerVariable(id);
            ret.add(new IdentifierToken(id));
            return ret;
        }
        if(((OperatorToken) second).type() == OperatorType.SEMICOLON)
        {
            ret.add(new OperatorToken(OperatorType.SEMICOLON));
            String id=((IdentifierToken) next).id();
            registerVariable(id);
            ret.add(new IdentifierToken(id));
            return ret;
        }
        throw new GrammarException(this.getClass().getSimpleName()+next+second);
    }
}

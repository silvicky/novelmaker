package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.registerVariable;

public class VariableDeclaration extends NonTerminal
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(!(next instanceof IdentifierToken identifierToken))
        {
            throw new GrammarException(this.getClass().getSimpleName()+next);
        }
        if(!(second instanceof OperatorToken operatorToken))
        {
            throw new GrammarException(this.getClass().getSimpleName()+next+second);
        }
        if(operatorToken.type == OperatorType.COMMA)
        {

            ret.add(new VariableDeclaration());
            ret.add(new OperatorToken(OperatorType.COMMA));
            String id=identifierToken.id;
            registerVariable(id);
            ret.add(new IdentifierToken(id));
            return ret;
        }
        if(operatorToken.type == OperatorType.SEMICOLON)
        {
            ret.add(new OperatorToken(OperatorType.SEMICOLON));
            String id=identifierToken.id;
            registerVariable(id);
            ret.add(new IdentifierToken(id));
            return ret;
        }
        throw new GrammarException(this.getClass().getSimpleName()+next+second);
    }
}

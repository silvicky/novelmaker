package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Expression extends NonTerminal
{
    @Override
    public List<Token> lookup(Token next, Token second) throws GrammarException
    {
        //TODO
        List<Token> ret=new ArrayList<>();
        if(next instanceof IdentifierToken)
        {
            if(second instanceof OperatorToken&&((OperatorToken) second).type()==OperatorType.EQUAL)
            {
                ret.add(new Expression());
                ret.add(new OperatorToken(OperatorType.EQUAL));
            }
            ret.add(new IdentifierToken(((IdentifierToken) next).id()));
            return ret;
        }
        if(next instanceof NumberToken)
        {
            ret.add(new NumberToken(((NumberToken) next).value()));
            return ret;
        }
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(new Expression());
        ret.add(new BinaryOperator());
        ret.add(new Expression());
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        return ret;
    }
}

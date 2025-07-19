package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class Block implements NonTerminal
{
    @Override
    public List<Token> lookup(Token next, Token second) throws GrammarException
    {
        List<Token> ret=new ArrayList<>();
        if(next instanceof OperatorToken&&((OperatorToken) next).type()== OperatorType.R_BRACE)return ret;
        ret.add(new Block());
        ret.add(new Line());
        return ret;
    }
}

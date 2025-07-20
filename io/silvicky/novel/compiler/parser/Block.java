package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.EofToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class Block extends NonTerminal
{
    @Override
    public List<Token> lookup(Token next, Token second) throws GrammarException
    {
        List<Token> ret=new ArrayList<>();
        if(next instanceof OperatorToken&&((OperatorToken) next).type()== OperatorType.R_BRACE)return ret;
        if(next instanceof EofToken)return ret;
        Block residue=new Block();
        Line current=new Line();
        ret.add(new AppendCodeSeqOperation(this,residue));
        ret.add(residue);
        ret.add(new AppendCodeSeqOperation(this,current));
        ret.add(current);
        return ret;
    }
}

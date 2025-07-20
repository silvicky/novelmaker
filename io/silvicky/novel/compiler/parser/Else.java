package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Else extends NonTerminal
{
    @Override
    public List<Token> lookup(Token next, Token second)
    {
        List<Token> ret=new ArrayList<>();
        if(next instanceof KeywordToken keywordToken&&keywordToken.type()== KeywordType.ELSE)
        {
            //TODO else if
            Line line=new Line();
            ret.add(new AppendCodeSeqOperation(this,line));
            ret.add(line);
            ret.add(new KeywordToken(KeywordType.ELSE));
            return ret;
        }
        else return ret;
    }
}

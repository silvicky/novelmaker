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
            Block block=new Block();
            ret.add(new AppendCodeSeqOperation(this,block));
            ret.add(new OperatorToken(OperatorType.R_BRACE));
            ret.add(block);
            ret.add(new OperatorToken(OperatorType.L_BRACE));
            ret.add(new KeywordToken(KeywordType.ELSE));
            return ret;
        }
        else return ret;
    }
}

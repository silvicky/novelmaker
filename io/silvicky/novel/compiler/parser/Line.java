package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Line implements NonTerminal
{

    @Override
    public List<Token> lookup(Token next, Token second) throws GrammarException
    {
        List<Token> ret=new ArrayList<>();
        if(next instanceof KeywordToken)
        {
            KeywordType type=((KeywordToken) next).type();
            if(type==KeywordType.FOR)
            {
                ret.add(new OperatorToken(OperatorType.R_BRACE));
                ret.add(new Block());
                ret.add(new OperatorToken(OperatorType.L_BRACE));
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(new Expression());
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                ret.add(new Expression());
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                ret.add(new Expression());
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                ret.add(new KeywordToken(KeywordType.FOR));
                return ret;
            }
            if(type==KeywordType.WHILE)
            {
                ret.add(new OperatorToken(OperatorType.R_BRACE));
                ret.add(new Block());
                ret.add(new OperatorToken(OperatorType.L_BRACE));
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(new Expression());
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                ret.add(new KeywordToken(KeywordType.WHILE));
                return ret;
            }
            if(type==KeywordType.DO)
            {
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(new Expression());
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                ret.add(new KeywordToken(KeywordType.WHILE));
                ret.add(new OperatorToken(OperatorType.R_BRACE));
                ret.add(new Block());
                ret.add(new OperatorToken(OperatorType.L_BRACE));
                ret.add(new KeywordToken(KeywordType.DO));
                return ret;
            }
            if(type == KeywordType.INT)
            {
                ret.add(new Declaration());
                ret.add(new KeywordToken(KeywordType.INT));
                return ret;
            }
            //TODO
            return null;
        }
        else
        {
            ret.add(new Expression());
            return ret;
        }
    }
}

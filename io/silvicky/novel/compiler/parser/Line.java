package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Line extends NonTerminal
{

    @Override
    public List<Token> lookup(Token next, Token second)
    {
        List<Token> ret=new ArrayList<>();
        if(next instanceof KeywordToken)
        {//TODO
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
                ret.add(new VariableDeclaration());
                ret.add(new KeywordToken(KeywordType.INT));
                return ret;
            }
            if(type==KeywordType.IF)
            {
                //TODO
                ret.add(new OperatorToken(OperatorType.R_BRACE));
                ret.add(new Block());
                ret.add(new OperatorToken(OperatorType.L_BRACE));
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(new Expression());
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                ret.add(new KeywordToken(KeywordType.IF));
                return ret;
            }
            //TODO
            return null;
        }
        else
        {
            ret.add(new OperatorToken(OperatorType.SEMICOLON));
            Expression e=new Expression();
            ret.add(new AppendCodeSeqOperation(this,e));
            ret.add(e);
            return ret;
        }
    }
}

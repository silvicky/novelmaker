package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.tokens.InvalidTokenException;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.Token;
import io.silvicky.novel.compiler.tokens.TokenBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Compiler
{
    public static List<Token> lexer(String input) throws InvalidTokenException
    {
        List<Token> ret=new ArrayList<>();
        TokenBuilder tokenBuilder=new TokenBuilder();
        for(char c:input.toCharArray())
        {
            if(Character.isLetter(c)||c=='_'||Character.isDigit(c)||OperatorType.find(String.valueOf(c))!=null)
            {
                if(!tokenBuilder.append(c))
                {
                    Token token= tokenBuilder.build();
                    if(token!=null)ret.add(token);
                    tokenBuilder=new TokenBuilder();
                    tokenBuilder.append(c);
                }
            }
            else
            {
                Token token= tokenBuilder.build();
                if(token!=null)ret.add(token);
                tokenBuilder=new TokenBuilder();
            }
        }
        Token token= tokenBuilder.build();
        if(token!=null)ret.add(token);
        return ret;
    }
    public static void main(String[] args) throws IOException, InvalidTokenException
    {
        BufferedReader bufferedReader=new BufferedReader(new FileReader(args[0]));
        StringBuilder stringBuilder=new StringBuilder();
        String cur;
        while(true)
        {
            cur= bufferedReader.readLine();
            if(cur==null)break;
            stringBuilder.append(cur);
        }
        List<Token> tokenList=lexer(stringBuilder.toString());
        for(Token token:tokenList)System.out.println(token);
    }
}

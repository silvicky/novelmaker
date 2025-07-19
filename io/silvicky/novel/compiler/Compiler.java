package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.Program;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.code.Code;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static io.silvicky.novel.util.Util.addNonNull;

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
                    addNonNull(ret, tokenBuilder.build());
                    tokenBuilder=new TokenBuilder();
                    tokenBuilder.append(c);
                }
            }
            else
            {
                addNonNull(ret, tokenBuilder.build());
                tokenBuilder=new TokenBuilder();
            }
        }
        addNonNull(ret, tokenBuilder.build());
        ret.add(new EofToken());
        ret.add(new EofToken());
        return ret;
    }
    public static List<Code> parser(List<Token> tokens)
    {
        List<Code> ret=new ArrayList<>();
        Stack<Token> stack=new Stack<>();
        stack.push(new EofToken());
        stack.push(new Program());
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

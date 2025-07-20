package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.parser.Block;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.Program;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.code.Code;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import static io.silvicky.novel.util.Util.addNonNull;

public class Compiler
{
    private static long label=0,variable=0;
    public static long newLabel(){return label++;}
    public static long newVariable(){return variable++;}
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
    public static List<Code> parser(List<Token> tokens) throws GrammarException
    {
        int rul=0;
        List<Code> ret=new ArrayList<>();
        Stack<Token> stack=new Stack<>();
        //stack.push(new Program());
        stack.push(new Block());
        while(!stack.empty())
        {
            System.out.println(stack);
            System.out.println(rul);
            Token top=stack.pop();
            Token next=tokens.get(rul);
            Token second=tokens.get(rul+1);
            if(!(top instanceof NonTerminal))
            {
                if(!top.equals(next))throw new GrammarException("Mismatch:"+top+next);
                rul++;
                continue;
            }
            List<Token> list=((NonTerminal) top).lookup(next,second);
            for(Token token:list)stack.push(token);
        }
        return ret;
    }
    public static void main(String[] args) throws IOException, InvalidTokenException, GrammarException
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
        //for(Token token:tokenList)System.out.println(token);
        parser(tokenList);
    }
}

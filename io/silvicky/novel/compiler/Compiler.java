package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.parser.Block;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.Program;
import io.silvicky.novel.compiler.parser.operation.Operation;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.code.Code;

import java.io.*;
import java.util.*;

import static io.silvicky.novel.util.Util.addNonNull;

public class Compiler
{
    private static long labelCnt=0,variableCnt=0;
    private static final Map<String,Long> labelMap=new HashMap<>();
    private static final Map<String,Long> variableMap=new HashMap<>();
    private static final int maxLabel=30,maxVariable=30;
    public static final long[] variableMem= new long[maxVariable];
    public static long registerVariable(String s)
    {
        if(variableMap.containsKey(s))throw new DeclarationException("Repeated:"+s);
        variableMap.put(s,variableCnt);
        return variableCnt++;
    }
    public static long lookupVariable(String s)
    {
        if(!variableMap.containsKey(s))throw new DeclarationException("Undefined:"+s);
        return variableMap.get(s);
    }
    public static long requestInternalVariable(){return variableCnt++;}
    public static List<Token> lexer(String input)
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
        int rul=0;
        Stack<Token> stack=new Stack<>();
        //stack.push(new Program());
        Block root=new Block();
        stack.push(root);
        while(!stack.empty())
        {
            Token top=stack.pop();
            if(top instanceof Operation)
            {
                ((Operation) top).execute();
                continue;
            }
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
        return root.codes;
    }
    public static void main(String[] args) throws IOException
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
        List<Code> codeList=parser(tokenList);
        System.out.println(codeList);
    }
}

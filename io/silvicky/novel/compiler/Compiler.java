package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.code.*;
import io.silvicky.novel.compiler.parser.Block;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.Program;
import io.silvicky.novel.compiler.parser.operation.Operation;
import io.silvicky.novel.compiler.tokens.*;

import java.io.*;
import java.util.*;

import static io.silvicky.novel.util.Util.addNonNull;

public class Compiler
{
    private static int labelCnt=0,variableCnt=0;
    private static final Map<String,Integer> labelMap=new HashMap<>();
    private static final Map<Integer,String> labelBackMap=new HashMap<>();
    private static final Map<String,Integer> variableMap=new HashMap<>();
    private static final Map<Integer,String> variableBackMap=new HashMap<>();
    public static int registerVariable(String s)
    {
        if(variableMap.containsKey(s))throw new DeclarationException("Repeated:"+s);
        variableMap.put(s,variableCnt);
        variableBackMap.put(variableCnt,s);
        return variableCnt++;
    }
    public static int lookupVariable(String s)
    {
        if(!variableMap.containsKey(s))throw new DeclarationException("Undefined:"+s);
        return variableMap.get(s);
    }
    public static String lookupVariableName(int l)
    {
        if(!variableBackMap.containsKey(l))return "V"+l;
        return variableBackMap.get(l);
    }
    public static int requestInternalVariable(){return variableCnt++;}
    public static int registerLabel(String s)
    {
        if(labelMap.containsKey(s))throw new DeclarationException("Repeated:"+s);
        labelMap.put(s,labelCnt);
        labelBackMap.put(labelCnt,s);
        return labelCnt++;
    }
    public static int lookupLabel(String s)
    {
        if(!labelMap.containsKey(s))throw new DeclarationException("Undefined:"+s);
        return labelMap.get(s);
    }
    public static String lookupLabelName(int l)
    {
        if(!labelBackMap.containsKey(l))return "L"+l;
        return labelBackMap.get(l);
    }
    public static int requestInternalLabel(){return labelCnt++;}
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
    public static void tokenParser(List<Token> tokens)
    {
        for(int i=0;i<tokens.size();i++)
        {
            Token token=tokens.get(i);
            if(token instanceof KeywordToken keywordToken&&keywordToken.type()==KeywordType.TRUE)
            {
                tokens.set(i,new NumberToken(1));
                continue;
            }
            if(token instanceof KeywordToken keywordToken&&keywordToken.type()==KeywordType.FALSE)
            {
                tokens.set(i,new NumberToken(0));
                continue;
            }
            if(token instanceof OperatorToken operatorToken&&operatorToken.type()==OperatorType.LABEL)
            {
                Token last=tokens.get(i-1);
                if(!(last instanceof IdentifierToken identifierToken))throw new GrammarException("label not named with an identifier");
                registerLabel(identifierToken.id());
            }
        }
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
    public static void emulateTAC(List<Code> codes)
    {
        Map<Integer,Integer> labelPos=new HashMap<>();
        long[] mem=new long[1048576];
        for(int i=0;i<codes.size();i++)if(codes.get(i) instanceof LabelCode labelCode)labelPos.put(labelCode.id(),i);
        int ip=0;
        while(ip<codes.size())
        {
            Code code=codes.get(ip++);
            if(code instanceof LabelCode)continue;
            if(code instanceof UnconditionalGotoCode unconditionalGotoCode)
            {
                ip=labelPos.get(unconditionalGotoCode.id());
                continue;
            }
            if(code instanceof GotoCode gotoCode)
            {
                if(gotoCode.op().operation.cal(mem[gotoCode.left()],mem[gotoCode.right()],0)!=0)ip=labelPos.get(gotoCode.id());
                continue;
            }
            if(code instanceof AssignNumberCode assignNumberCode)
            {
                mem[assignNumberCode.target()]=assignNumberCode.left();
                continue;
            }
            if(code instanceof AssignCode assignCode)
            {
                mem[assignCode.target()]=assignCode.op().operation.cal(mem[assignCode.left()],mem[assignCode.right()],0);
                continue;
            }
            if(code instanceof AssignVariableNumberCode assignVariableNumberCode)
            {
                mem[assignVariableNumberCode.target()]=assignVariableNumberCode.op().operation.cal(mem[assignVariableNumberCode.left()],assignVariableNumberCode.right(),0);
                continue;
            }
            System.out.println("Unknown TAC: "+code.toString());
        }
        for(Map.Entry<String,Integer> entry:variableMap.entrySet())
        {
            System.out.println(entry.getKey()+"="+mem[entry.getValue()]);
        }
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
        tokenParser(tokenList);
        List<Code> codeList=parser(tokenList);
        //for(Code code:codeList)System.out.println(code);
        emulateTAC(codeList);
    }
}

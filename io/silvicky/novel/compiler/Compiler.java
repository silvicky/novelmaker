package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.code.*;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.Program;
import io.silvicky.novel.compiler.parser.operation.LocalVariableClearOperation;
import io.silvicky.novel.compiler.parser.operation.Operation;
import io.silvicky.novel.compiler.tokens.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static io.silvicky.novel.util.Util.addNonNull;

public class Compiler
{
    //TODO Fully rewrite
    private static int labelCnt=0;
    private static int variableCnt=0;
    public static int ctx=-1;
    public static final int dataSegmentBaseAddress=0xF0000;
    private static final Map<String,Integer> labelMap=new HashMap<>();
    private static final Map<Integer,String> labelBackMap=new HashMap<>();
    private static final Map<Integer,Map<String,Integer>> localLabelMap=new HashMap<>();
    private static final Map<Integer,Map<Integer,String>> localLabelBackMap=new HashMap<>();
    private static final Map<String,Integer> variableMap=new HashMap<>();
    private static final Map<Integer,String> variableBackMap=new HashMap<>();
    private static final Map<Integer,Map<String,Stack<Integer>>> localVariableMap=new HashMap<>();
    private static final Map<Integer,Map<Integer,String>> localVariableBackMap=new HashMap<>();
    private static final Map<Integer,Integer> localVariableCount=new HashMap<>();
    public static int registerVariable(String s)
    {
        if(variableMap.containsKey(s))throw new DeclarationException("Repeated:"+s);
        variableMap.put(s,variableCnt+dataSegmentBaseAddress);
        variableBackMap.put(variableCnt+dataSegmentBaseAddress,s);
        return dataSegmentBaseAddress+variableCnt++;
    }
    public static int registerLocalVariable(String s)
    {
        if(!localVariableMap.containsKey(ctx))localVariableMap.put(ctx,new HashMap<>());
        if(!localVariableMap.get(ctx).containsKey(s))localVariableMap.get(ctx).put(s,new Stack<>());
        if(!localVariableCount.containsKey(ctx))localVariableCount.put(ctx,0);
        final int cnt=localVariableCount.get(ctx);
        localVariableMap.get(ctx).get(s).push(cnt);
        if(!localVariableBackMap.containsKey(ctx))localVariableBackMap.put(ctx,new HashMap<>());
        localVariableBackMap.get(ctx).put(cnt,String.format("%s(V%d)",s,cnt));
        localVariableCount.put(ctx,cnt+1);
        return cnt;
    }
    public static void revokeLocalVariable(String s)
    {
        if(!localVariableMap.containsKey(ctx))throw new DeclarationException("Undefined:"+s);
        if(!localVariableMap.get(ctx).containsKey(s))throw new DeclarationException("Undefined:"+s);
        if(localVariableMap.get(ctx).get(s).empty())throw new DeclarationException("Undefined:"+s);
        localVariableMap.get(ctx).get(s).pop();
    }
    public static int lookupVariable(String s)
    {
        if(localVariableMap.containsKey(ctx)&&localVariableMap.get(ctx).containsKey(s)&&!localVariableMap.get(ctx).get(s).empty())return localVariableMap.get(ctx).get(s).peek();
        if(variableMap.containsKey(s))return variableMap.get(s);
        throw new DeclarationException("Undefined:"+s);
    }
    public static String lookupVariableName(int l)
    {
        if(localVariableBackMap.containsKey(ctx)&&localVariableBackMap.get(ctx).containsKey(l))return localVariableBackMap.get(ctx).get(l);
        if(variableBackMap.containsKey(l))return variableBackMap.get(l);
        return "V"+l;
    }
    public static int requestInternalVariable()
    {
        if(!localVariableCount.containsKey(ctx))localVariableCount.put(ctx,0);
        final int cnt=localVariableCount.get(ctx);
        localVariableCount.put(ctx,cnt+1);
        return cnt;
    }
    public static int registerLabel(String s)
    {
        if(labelMap.containsKey(s))throw new DeclarationException("Repeated:"+s);
        labelMap.put(s,labelCnt);
        labelBackMap.put(labelCnt,s);
        return labelCnt++;
    }
    public static int requestLabel()
    {
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
    public static List<AbstractToken> lexer(Path input) throws IOException
    {
        List<AbstractToken> ret=new ArrayList<>();
        TokenBuilder tokenBuilder=new TokenBuilder(input.toString(),1,1);
        BufferedReader bufferedReader=new BufferedReader(new FileReader(input.toFile()));
        String cur;
        for(int line=1;;line++)
        {
            cur= bufferedReader.readLine();
            if(cur==null)break;
            for(int pos=0;pos<cur.length();pos++)
            {
                char c=cur.charAt(pos);
                if(Character.isLetter(c)||c=='_'||Character.isDigit(c)||OperatorType.find(String.valueOf(c))!=null)
                {
                    if(!tokenBuilder.append(c))
                    {
                        addNonNull(ret, tokenBuilder.build());
                        tokenBuilder=new TokenBuilder(input.toString(),line,pos+1);
                        tokenBuilder.append(c);
                    }
                }
                else
                {
                    addNonNull(ret, tokenBuilder.build());
                    tokenBuilder=new TokenBuilder(input.toString(),line,pos+1);
                }
            }
        }
        addNonNull(ret, tokenBuilder.build());
        ret.add(new EofToken());
        ret.add(new EofToken());
        bufferedReader.close();
        return ret;
    }
    public static void tokenParser(List<AbstractToken> abstractTokens)
    {
        for(int i = 0; i< abstractTokens.size(); i++)
        {
            AbstractToken abstractToken = abstractTokens.get(i);
            if(abstractToken instanceof KeywordToken keywordToken&&keywordToken.type==KeywordType.TRUE)
            {
                abstractTokens.set(i,new NumberToken(1,keywordToken.fileName,keywordToken.line,keywordToken.pos));
                continue;
            }
            if(abstractToken instanceof KeywordToken keywordToken&&keywordToken.type==KeywordType.FALSE)
            {
                abstractTokens.set(i,new NumberToken(0,keywordToken.fileName,keywordToken.line,keywordToken.pos));
                continue;
            }
            if(abstractToken instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.LABEL)
            {
                AbstractToken last= abstractTokens.get(i-1);
                if(!(last instanceof IdentifierToken identifierToken))throw new GrammarException("label not named with an identifier");
                //TODO what is this
                registerLabel(identifierToken.id);
            }
        }
    }
    public static boolean match(AbstractToken a, AbstractToken b)
    {
        if(!(a.getClass().equals(b.getClass())))return false;
        if(a instanceof IdentifierToken)return ((IdentifierToken) a).id.equals(((IdentifierToken) b).id);
        if(a instanceof KeywordToken)return ((KeywordToken) a).type.equals(((KeywordToken) b).type);
        if(a instanceof OperatorToken)return ((OperatorToken) a).type.equals(((OperatorToken) b).type);
        if(a instanceof NumberToken)return ((NumberToken) a).value==((NumberToken) b).value;
        return false;
    }
    public static List<Code> parser(List<AbstractToken> abstractTokens)
    {
        int rul=0;
        Stack<AbstractToken> stack=new Stack<>();
        Program root=new Program();
        stack.push(root);
        while(!stack.empty())
        {
            AbstractToken top=stack.pop();
            if(top instanceof Operation)
            {
                ((Operation) top).execute();
                continue;
            }
            AbstractToken next= abstractTokens.get(rul);
            AbstractToken second= abstractTokens.get(rul+1);
            if(!(top instanceof NonTerminal nonTerminal))
            {
                if(!match(top,next))throw new GrammarException("Mismatch: "+top+" and "+next);
                rul++;
                continue;
            }
            List<AbstractToken> list=nonTerminal.lookup(next,second);
            stack.push(new LocalVariableClearOperation(nonTerminal));
            for(AbstractToken abstractToken :list)stack.push(abstractToken);
        }
        return root.codes;
    }
    public static void emulateTAC(List<Code> codes)
    {
        Map<Integer,Integer> labelPos=new HashMap<>();
        long[] mem=new long[1048576];
        for(int i=0;i<codes.size();i++)if(codes.get(i) instanceof LabelCode labelCode)labelPos.put(labelCode.id(),i);
        if(!labelMap.containsKey("main"))throw new DeclarationException("no main function defined");
        codes.add(new UnconditionalGotoCode(labelMap.get("main")));
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
            if(code instanceof ReturnCode)
            {
                //TODO
                break;
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
        List<AbstractToken> abstractTokenList =lexer(Path.of(args[0]));
        tokenParser(abstractTokenList);
        List<Code> codeList=parser(abstractTokenList);
        //for(Code code:codeList)System.out.println(code);
        emulateTAC(codeList);
    }
}

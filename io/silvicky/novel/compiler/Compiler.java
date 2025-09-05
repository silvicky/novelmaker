package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.code.*;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.Program;
import io.silvicky.novel.compiler.parser.Skip;
import io.silvicky.novel.compiler.parser.operation.Operation;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static io.silvicky.novel.compiler.types.PrimitiveType.BOOL;
import static io.silvicky.novel.util.Util.addNonNull;

public class Compiler
{
    private static int labelCnt=0;
    private static int variableCnt=0;
    public static int ctx=-1;
    public static final int dataSegmentBaseAddress=0xF0000;
    private static final Map<String,Integer> labelMap=new HashMap<>();
    private static final Map<Integer,String> labelBackMap=new HashMap<>();
    private static final Map<Integer,Map<String,Integer>> localLabelMap=new HashMap<>();
    private static final Map<String, Pair<Integer, Type>> variableMap=new HashMap<>();
    private static final Map<Integer, Pair<String, Type>> variableBackMap=new HashMap<>();
    private static final Map<Integer,Map<String,Stack<Pair<Integer, Type>>>> localVariableMap=new HashMap<>();
    private static final Map<Integer,Map<Integer, Pair<String, Type>>> localVariableBackMap=new HashMap<>();
    private static final Map<Integer,Integer> localVariableCount=new HashMap<>();
    public static int registerVariable(String s, Type type)
    {
        if(variableMap.containsKey(s))throw new DeclarationException("Repeated:"+s);
        variableMap.put(s,new Pair<>(variableCnt+dataSegmentBaseAddress,type));
        variableBackMap.put(variableCnt+dataSegmentBaseAddress,new Pair<>(s,type));
        return dataSegmentBaseAddress+variableCnt++;
    }
    public static int registerLocalVariable(String s, Type type)
    {
        if(!localVariableMap.containsKey(ctx))localVariableMap.put(ctx,new HashMap<>());
        if(!localVariableMap.get(ctx).containsKey(s))localVariableMap.get(ctx).put(s,new Stack<>());
        if(!localVariableCount.containsKey(ctx))localVariableCount.put(ctx,1);
        final int cnt=localVariableCount.get(ctx);
        localVariableMap.get(ctx).get(s).push(new Pair<>(cnt,type));
        if(!localVariableBackMap.containsKey(ctx))localVariableBackMap.put(ctx,new HashMap<>());
        localVariableBackMap.get(ctx).put(cnt,new Pair<>(String.format("%s(V%d)",s,cnt),type));
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
    public static Pair<Integer,Type> lookupVariable(String s)
    {
        if(localVariableMap.containsKey(ctx)&&localVariableMap.get(ctx).containsKey(s)&&!localVariableMap.get(ctx).get(s).empty())return localVariableMap.get(ctx).get(s).peek();
        if(variableMap.containsKey(s))return variableMap.get(s);
        throw new DeclarationException("Undefined:"+s);
    }
    public static String lookupVariableName(int l)
    {
        if(localVariableBackMap.containsKey(ctx)&&localVariableBackMap.get(ctx).containsKey(l))return localVariableBackMap.get(ctx).get(l).first();
        if(variableBackMap.containsKey(l))return variableBackMap.get(l).first();
        return "V"+l;
    }
    public static int requestInternalVariable()
    {
        if(!localVariableCount.containsKey(ctx))localVariableCount.put(ctx,1);
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
    public static int registerLocalLabel(String s)
    {
        if(!localLabelMap.containsKey(ctx))localLabelMap.put(ctx,new HashMap<>());
        if(localLabelMap.get(ctx).containsKey(s))throw new DeclarationException("Repeated:"+s);
        localLabelMap.get(ctx).put(s,labelCnt);
        labelBackMap.put(labelCnt,String.format("%s(L%d)",s,labelCnt));
        return labelCnt++;
    }
    public static int requestLabel()
    {
        return labelCnt++;
    }
    public static int lookupLabel(String s)
    {
        if(localLabelMap.containsKey(ctx)&&localLabelMap.get(ctx).containsKey(s))return localLabelMap.get(ctx).get(s);
        if(labelMap.containsKey(s))return labelMap.get(s);
        throw new DeclarationException("Undefined:"+s);
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
                abstractTokens.set(i,new NumberToken<>(1,BOOL,keywordToken.fileName,keywordToken.line,keywordToken.pos));
                continue;
            }
            if(abstractToken instanceof KeywordToken keywordToken&&keywordToken.type==KeywordType.FALSE)
            {
                abstractTokens.set(i,new NumberToken<>(0,BOOL,keywordToken.fileName,keywordToken.line,keywordToken.pos));
            }
        }
    }
    public static boolean match(AbstractToken a, AbstractToken b)
    {
        if(!(a.getClass().equals(b.getClass())))return false;
        if(a instanceof IdentifierToken)return ((IdentifierToken) a).id.equals(((IdentifierToken) b).id);
        if(a instanceof KeywordToken)return ((KeywordToken) a).type.equals(((KeywordToken) b).type);
        if(a instanceof OperatorToken)return ((OperatorToken) a).type.equals(((OperatorToken) b).type);
        return a instanceof NumberToken;
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
            if(top instanceof Operation operation)
            {
                operation.execute();
                continue;
            }
            if(top instanceof Skip)
            {
                stack.pop();
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
            for(AbstractToken abstractToken :list)stack.push(abstractToken);
        }
        for(int i=0;i<root.codes.size();i++)
        {
            if(root.codes.get(i) instanceof PlaceholderUnconditionalGotoCode tmp)
            {
                int target=localLabelMap.get(tmp.ctx()).get(tmp.labelName());
                root.codes.set(i,new UnconditionalGotoCode(target));
            }
        }
        root.travel();
        return root.codes;
    }
    public static int addressTransformer(int bp,int val)
    {
        if(val>=dataSegmentBaseAddress)return val;
        return bp-val;
    }
    public static void emulateTAC(List<Code> codes)
    {
        Map<Integer,Integer> labelPos=new HashMap<>();
        long[] mem=new long[1048576];
        for(int i=0;i<codes.size();i++)if(codes.get(i) instanceof LabelCode labelCode)labelPos.put(labelCode.id(),i);
        if(!variableMap.containsKey("main"))throw new DeclarationException("no main function defined");
        codes.add(new CallCode(variableMap.get("main").first(),new ArrayList<>()));
        printCodeList(codes);
        int ip=0;
        int bp=dataSegmentBaseAddress-1;
        int sp=bp;
        int ret=0;
        while(ip<codes.size())
        {
            Code code=codes.get(ip++);
            if(code instanceof LabelCode)continue;
            //TODO Fully rewrite
            if(code instanceof UnconditionalGotoCode unconditionalGotoCode)
            {
                ip=labelPos.get(unconditionalGotoCode.id());
                continue;
            }
            if(code instanceof GotoCode gotoCode)
            {
                if(gotoCode.op().operation.cal(mem[addressTransformer(bp,gotoCode.left())],mem[addressTransformer(bp,gotoCode.right())],0)!=0)ip=labelPos.get(gotoCode.id());
                continue;
            }
            if(code instanceof AssignNumberCode assignNumberCode)
            {
                mem[addressTransformer(bp,assignNumberCode.target())]= (long)(int) assignNumberCode.left();
                continue;
            }
            if(code instanceof AssignCode assignCode)
            {
                mem[addressTransformer(bp,assignCode.target())]=assignCode.op().operation.cal(mem[addressTransformer(bp,assignCode.left())],mem[addressTransformer(bp,assignCode.right())],0);
                continue;
            }
            if(code instanceof AssignVariableNumberCode assignVariableNumberCode)
            {
                mem[addressTransformer(bp,assignVariableNumberCode.target())]=assignVariableNumberCode.op().operation.cal(mem[addressTransformer(bp,assignVariableNumberCode.left())], (Long) assignVariableNumberCode.right(),0);
                continue;
            }
            if(code instanceof ReturnCode returnCode)
            {
                ret=addressTransformer(bp,returnCode.val());
                sp=bp+2;
                ip=(int)mem[bp+1];
                bp=(int)mem[bp];
                continue;
            }
            if(code instanceof CallCode callCode)
            {
                int callTarget= (int) mem[addressTransformer(bp,callCode.target())];
                mem[--sp]=ip;
                mem[--sp]=bp;
                int oldBP=bp;
                bp=sp;
                sp=bp-localVariableCount.get(callTarget);
                for(int i=0;i<callCode.parameters().size();i++)
                {
                    mem[bp-i-1]=mem[addressTransformer(oldBP,callCode.parameters().get(i))];
                }
                ip=labelPos.get(callTarget);
                continue;
            }
            if(code instanceof FetchReturnValueCode fetchReturnValueCode)
            {
                mem[addressTransformer(bp,fetchReturnValueCode.target())]=mem[ret];
                continue;
            }
            if(code instanceof PlaceholderUnconditionalGotoCode)
            {
                throw new DeclarationException("Placeholder should be erased");
            }
            if(code instanceof DereferenceCode dereferenceCode)
            {
                mem[addressTransformer(bp,dereferenceCode.target())]=mem[(int) mem[addressTransformer(bp, dereferenceCode.left())]];
            }
            System.out.println("Unknown TAC: "+code.toString());
        }
        System.out.println("RESULT:");
        for(Map.Entry<String,Pair<Integer,Type>> entry:variableMap.entrySet())
        {
            System.out.println(entry.getKey()+"="+mem[entry.getValue().first()]);
        }
    }
    public static void printCodeList(List<Code> codeList)
    {
        for(Code code:codeList)
        {
            if(code instanceof LabelCode labelCode&&labelBackMap.containsKey(labelCode.id()))ctx=labelCode.id();
            if(code instanceof ReturnCode)ctx=-1;
            System.out.println(code);
        }
    }
    public static void main(String[] args) throws IOException
    {
        List<AbstractToken> abstractTokenList =lexer(Path.of(args[0]));
        tokenParser(abstractTokenList);
        List<Code> codeList=parser(abstractTokenList);
        emulateTAC(codeList);
    }
}

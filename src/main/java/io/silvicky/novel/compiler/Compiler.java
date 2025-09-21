package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.code.*;
import io.silvicky.novel.compiler.code.primitive.*;
import io.silvicky.novel.compiler.code.raw.*;
import io.silvicky.novel.compiler.emulator.VirtualMemory;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.Program;
import io.silvicky.novel.compiler.parser.operation.Operation;
import io.silvicky.novel.compiler.parser.operation.Skip;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.*;
import io.silvicky.novel.util.Pair;
import io.silvicky.novel.util.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static io.silvicky.novel.compiler.types.PrimitiveType.BOOL;
import static io.silvicky.novel.compiler.types.PrimitiveType.INT;
import static io.silvicky.novel.compiler.types.Type.ADDRESS_WIDTH;
import static io.silvicky.novel.util.Util.addNonNull;

public class Compiler
{
    private static int labelCnt=0;
    private static int variableCnt=0;
    public static int ctx=-1;
    public static Type returnType=null;
    public static int argSize;
    public static final int dataSegmentBaseAddress=0xF0000;
    private static final Map<String,Integer> labelMap=new HashMap<>();
    private static final Map<Integer,String> labelBackMap=new HashMap<>();
    private static final Map<Integer,Map<String,Integer>> localLabelMap=new HashMap<>();
    private static final Map<String, Pair<Integer, Type>> variableMap=new HashMap<>();
    private static final Map<Integer, Pair<String, Type>> variableBackMap=new HashMap<>();
    private static final Map<Integer, Integer> variableAddress=new HashMap<>();
    private static final Map<Integer,Map<String,Stack<Pair<Integer, Type>>>> localVariableMap=new HashMap<>();
    private static final Map<Integer,Map<Integer, Pair<String, Type>>> localVariableBackMap=new HashMap<>();
    private static final Map<Integer,Map<Integer,Integer>> localVariableAddress=new HashMap<>();
    private static final Map<Integer,Integer> localVariableSize=new HashMap<>();
    private static final Map<Integer,Integer> localVariableCount=new HashMap<>();
    public static int registerVariable(String s, Type type)
    {
        if(type==PrimitiveType.VOID)throw new DeclarationException("declaring void variable");
        if(variableMap.containsKey(s))throw new DeclarationException("Repeated:"+s);
        int ret=variableCnt+dataSegmentBaseAddress;
        variableMap.put(s,new Pair<>(ret,type));
        variableBackMap.put(ret,new Pair<>(s,type));
        variableCnt++;
        return ret;
    }
    public static int registerLocalVariable(String s, Type type)
    {
        if(type==PrimitiveType.VOID)throw new DeclarationException("declaring void variable");
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
    public static void registerArgument(String s, Type type)
    {
        if(type==PrimitiveType.VOID)throw new DeclarationException("declaring void variable");
        if(!localVariableMap.containsKey(ctx))localVariableMap.put(ctx,new HashMap<>());
        if(!localVariableMap.get(ctx).containsKey(s))localVariableMap.get(ctx).put(s,new Stack<>());
        //0 is bp and -1 is ip
        int id=-localVariableMap.get(ctx).size()-1;
        localVariableMap.get(ctx).get(s).push(new Pair<>(id,type));
        if(!localVariableBackMap.containsKey(ctx))localVariableBackMap.put(ctx,new HashMap<>());
        localVariableBackMap.get(ctx).put(id,new Pair<>(String.format("%s(V%d)",s,id),type));
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
    public static int requestInternalVariable(Type type)
    {
        if(type==PrimitiveType.VOID)throw new DeclarationException("declaring void variable");
        if(ctx==-1)
        {
            final int ret=variableCnt+dataSegmentBaseAddress;
            variableBackMap.put(ret,new Pair<>("V"+variableCnt,type));
            variableCnt++;
            return ret;
        }
        if(!localVariableCount.containsKey(ctx))localVariableCount.put(ctx,1);
        final int cnt=localVariableCount.get(ctx);
        if(!localVariableBackMap.containsKey(ctx))localVariableBackMap.put(ctx,new HashMap<>());
        localVariableBackMap.get(ctx).put(cnt,new Pair<>("V"+cnt,type));
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
    public static String lookupLabelName(int l)
    {
        if(!labelBackMap.containsKey(l))return "L"+l;
        return labelBackMap.get(l);
    }
    public static int requestLabel(){return labelCnt++;}
    public static List<AbstractToken> lexer(Path input) throws IOException
    {
        List<AbstractToken> ret=new ArrayList<>();
        TokenBuilder tokenBuilder=null;
        BufferedReader bufferedReader=new BufferedReader(new FileReader(input.toFile()));
        String cur;
        char las=0;
        boolean isGlobalComment=false;
        boolean isBackslashConnected=false;
        for(int line=1;;line++)
        {
            if(!isBackslashConnected)
            {
                las = 0;
            }
            isBackslashConnected=false;
            cur = bufferedReader.readLine();
            if(cur==null)break;
            for(int pos=0;pos<cur.length();pos++)
            {
                char c=cur.charAt(pos);
                if(isGlobalComment)
                {
                    if(las=='*'&&c=='/')
                    {
                        isGlobalComment=false;
                        las=0;
                    }
                    else las=c;
                    continue;
                }
                if(las=='/'&&c=='/')
                {
                    tokenBuilder = null;
                    ret.add(PreprocessorToken.EOL);
                    break;
                }
                if(las=='/'&&c=='*')
                {
                    tokenBuilder = null;
                    isGlobalComment=true;
                    continue;
                }
                if(c=='\\'&&pos==cur.length()-1)
                {
                    isBackslashConnected=true;
                    continue;
                }
                if(tokenBuilder==null)tokenBuilder=new TokenBuilder(input.toString(), line, pos+1);
                if(!tokenBuilder.append(c))
                {
                    addNonNull(ret, tokenBuilder.build());
                    tokenBuilder = new TokenBuilder(input.toString(), line, pos + 1);
                    tokenBuilder.append(c);
                }
                las=c;
                if(pos==cur.length()-1)
                {
                    addNonNull(ret, tokenBuilder.build());
                    tokenBuilder=null;
                    ret.add(PreprocessorToken.EOL);
                }
            }
        }
        if(tokenBuilder!=null)addNonNull(ret, tokenBuilder.build());
        ret.add(PreprocessorToken.EOF);
        ret.add(PreprocessorToken.EOF);
        bufferedReader.close();
        return ret;
    }
    public static List<AbstractToken> tokenParser(List<AbstractToken> abstractTokens)
    {
        List<AbstractToken> ret=new ArrayList<>();
        for (AbstractToken abstractToken : abstractTokens)
        {
            if (abstractToken instanceof KeywordToken keywordToken)
            {
                switch (keywordToken.type)
                {
                    case TRUE ->
                            ret.add(new NumberToken<>(true, BOOL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case FALSE ->
                            ret.add(new NumberToken<>(false, BOOL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case AND ->
                            ret.add(new OperatorToken(OperatorType.AND_AND, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case AND_EQ ->
                            ret.add(new OperatorToken(OperatorType.AND_EQUAL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case BITAND ->
                            ret.add(new OperatorToken(OperatorType.AND, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case BITOR ->
                            ret.add(new OperatorToken(OperatorType.OR, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case COMPL ->
                            ret.add(new OperatorToken(OperatorType.REVERSE, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case NOT ->
                            ret.add(new OperatorToken(OperatorType.NOT, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case NOT_EQ ->
                            ret.add(new OperatorToken(OperatorType.NOT_EQUAL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case OR ->
                            ret.add(new OperatorToken(OperatorType.OR_OR, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case OR_EQ ->
                            ret.add(new OperatorToken(OperatorType.OR_EQUAL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case XOR ->
                            ret.add(new OperatorToken(OperatorType.XOR, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case XOR_EQ ->
                            ret.add(new OperatorToken(OperatorType.XOR_EQUAL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    default -> ret.add(keywordToken);
                }
            }
            else if (abstractToken instanceof OperatorToken operatorToken)
            {
                switch (operatorToken.type)
                {
                    case ALT_L_BRACKET ->
                            ret.add(new OperatorToken(OperatorType.L_BRACKET, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                    case ALT_R_BRACKET ->
                            ret.add(new OperatorToken(OperatorType.R_BRACKET, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                    case ALT_L_BRACE ->
                            ret.add(new OperatorToken(OperatorType.L_BRACE, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                    case ALT_R_BRACE ->
                            ret.add(new OperatorToken(OperatorType.R_BRACE, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                    case SHARP, ALT_SHARP -> ret.add(PreprocessorToken.SHARP);
                    case SHARP_SHARP, ALT_SHARP_SHARP -> ret.add(PreprocessorToken.SHARP_SHARP);
                    case ALT_SHARP_MOD ->
                    {
                        ret.add(PreprocessorToken.SHARP);
                        ret.add(new OperatorToken(OperatorType.MOD, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                    }
                    default -> ret.add(abstractToken);
                }
            }
            else ret.add(abstractToken);
        }
        return ret;
    }
    public static List<AbstractToken> preprocessor(List<AbstractToken> abstractTokens)
    {
        //TODO
        List<AbstractToken> ret=new ArrayList<>();
        for(AbstractToken abstractToken:abstractTokens)
        {
            if(abstractToken!=PreprocessorToken.EOL&&abstractToken!=PreprocessorToken.SHARP&&abstractToken!=PreprocessorToken.SHARP_SHARP)ret.add(abstractToken);
        }
        return ret;
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
        root.travel();
        ctx=-1;
        for(int i=0;i<root.codes.size();i++)
        {
            if(root.codes.get(i) instanceof LabelCode labelCode)
            {
                if(!labelBackMap.containsKey(labelCode.id()))continue;
                if(labelBackMap.get(labelCode.id()).charAt(0)=='0')ctx=-1;
                else if(labelMap.containsKey(labelBackMap.get(labelCode.id())))ctx=labelCode.id();
            }
            if(root.codes.get(i) instanceof PlaceholderUnconditionalGotoCode tmp)
            {
                int target=localLabelMap.get(ctx).get(tmp.labelName());
                root.codes.set(i,new UnconditionalGotoCode(target));
            }
        }
        return root.codes;
    }
    public static int addressTransformer(int bp,int val)
    {
        if(val>=dataSegmentBaseAddress)return val;
        return bp-val;
    }

    public static List<Code> typeEraser(List<Code> codes)
    {
        List<Code> ret=new ArrayList<>();
        ctx=-1;
        for(Code code:codes)
        {
            if(code instanceof LabelCode labelCode)
            {
                if(labelBackMap.containsKey(labelCode.id()))
                {
                    if (labelBackMap.get(labelCode.id()).charAt(0) == '0') ctx = -1;
                    else if(labelMap.containsKey(labelBackMap.get(labelCode.id())))ctx = labelCode.id();
                }
            }
            if(code instanceof RawCode rawCode)
            {
                ret.addAll(rawCode.analyze());
            }
            else
            {
                ret.add(code);
            }
        }
        return ret;
    }

    private static int lookupAddress(int id)
    {
        if(id==0||id==-1)return 0;
        if(id>=dataSegmentBaseAddress)return variableAddress.get(id);
        return localVariableAddress.get(ctx).get(id);
    }
    private static List<Code> assignAddress(List<Code> codes)
    {
        int cur=dataSegmentBaseAddress;
        int cur2;
        List<Map.Entry<Integer, Pair<String, Type>>> entries=new ArrayList<>(variableBackMap.entrySet());
        entries.sort(Comparator.comparingInt(Map.Entry::getKey));
        for(Map.Entry<Integer, Pair<String, Type>> pr:entries)
        {
            variableAddress.put(pr.getKey(),cur);
            if((pr.getValue().second() instanceof ArrayType)&&(!variableMap.containsKey(pr.getValue().first())))
            {
                cur+=ADDRESS_WIDTH;
            }
            else cur+=pr.getValue().second().getSize();
        }
        for(Map.Entry<Integer, Map<Integer, Pair<String, Type>>> pr:localVariableBackMap.entrySet())
        {
            cur=0;
            cur2=-(2*ADDRESS_WIDTH);
            if(!localVariableSize.containsKey(pr.getKey()))
                localVariableSize.put(pr.getKey(),0);
            if (!localVariableAddress.containsKey(pr.getKey()))
                localVariableAddress.put(pr.getKey(), new HashMap<>());
            List<Map.Entry<Integer, Pair<String, Type>>> entries2=new ArrayList<>(pr.getValue().entrySet());
            entries2.sort(Comparator.comparingInt(o -> Math.abs(o.getKey())));
            for(Map.Entry<Integer, Pair<String, Type>> pr2:entries2)
            {
                if(pr2.getKey()>=0)
                {
                    int siz;
                    if((pr2.getValue().second() instanceof ArrayType)&&(!localVariableMap.get(pr.getKey()).containsKey(pr2.getValue().first())))
                    {
                        siz=ADDRESS_WIDTH;
                    }
                    else siz = pr2.getValue().second().getSize();
                    cur+=siz;
                    localVariableAddress.get(pr.getKey()).put(pr2.getKey(), cur);
                    int curSize=localVariableSize.get(pr.getKey());
                    localVariableSize.put(pr.getKey(),curSize+siz);
                }
                else
                {
                    if (!localVariableAddress.containsKey(pr.getKey()))
                        localVariableAddress.put(pr.getKey(), new HashMap<>());
                    localVariableAddress.get(pr.getKey()).put(pr2.getKey(), cur2);
                    cur2-=pr2.getValue().second().getSize();
                }
            }
        }
        for(Map.Entry<String, Integer> pr:labelMap.entrySet())
        {
            if(pr.getKey().charAt(0)=='0')continue;
            if(!localVariableSize.containsKey(pr.getValue()))localVariableSize.put(pr.getValue(),0);
        }
        List<Code> ret=new ArrayList<>();
        ctx=-1;
        for(Code code:codes)
        {
            if(code instanceof UnconditionalGotoCode)
            {
                ret.add(code);
                continue;
            }
            if(code instanceof LabelCode labelCode)
            {
                if(labelBackMap.containsKey(labelCode.id()))
                {
                    if (labelBackMap.get(labelCode.id()).charAt(0) == '0') ctx = -1;
                    else if(labelMap.containsKey(labelBackMap.get(labelCode.id())))ctx = labelCode.id();
                }
                ret.add(code);
                continue;
            }
            if(code instanceof GotoCodeP gotoCode)
            {
                ret.add(new GotoCodeP(lookupAddress(gotoCode.left()), gotoCode.id()));
                continue;
            }
            if(code instanceof IndirectAssignCodeP indirectAssignCode)
            {
                ret.add(new IndirectAssignCodeP(lookupAddress(indirectAssignCode.target()),lookupAddress(indirectAssignCode.source()),indirectAssignCode.size()));
                continue;
            }
            if(code instanceof ReferenceCode referenceCode)
            {
                ret.add(new ReferenceCode(lookupAddress(referenceCode.target()),lookupAddress(referenceCode.left())));
                continue;
            }
            if(code instanceof AssignMMCodeP assignMMCodeP)
            {
                ret.add(new AssignMMCodeP(lookupAddress(assignMMCodeP.target()),lookupAddress(assignMMCodeP.left()),lookupAddress(assignMMCodeP.right()),assignMMCodeP.type(),assignMMCodeP.op()));
                continue;
            }
            if(code instanceof AssignMICodeP assignMICodeP)
            {
                ret.add(new AssignMICodeP(lookupAddress(assignMICodeP.target()),lookupAddress(assignMICodeP.left()),assignMICodeP.right(),assignMICodeP.type(),assignMICodeP.op()));
                continue;
            }
            if(code instanceof CastMMCodeP castMMCodeP)
            {
                ret.add(new CastMMCodeP(lookupAddress(castMMCodeP.target()),lookupAddress(castMMCodeP.source()), castMMCodeP.targetType(),castMMCodeP.sourceType()));
                continue;
            }
            if(code instanceof ReturnCode returnCode)
            {
                ret.add(new ReturnCode(lookupAddress(returnCode.val()),returnCode.size()));
                continue;
            }
            if(code instanceof CallCodeP callCodeP)
            {
                ret.add(new CallCodeP(lookupAddress(callCodeP.target())));
            }
            if(code instanceof PushCodeP pushCodeP)
            {
                ret.add(new PushCodeP(lookupAddress(pushCodeP.source()),pushCodeP.size()));
            }
            if(code instanceof FetchReturnValueCodeP fetchReturnValueCode)
            {
                ret.add(new FetchReturnValueCodeP(lookupAddress(fetchReturnValueCode.target()), fetchReturnValueCode.size()));
                continue;
            }
            if(code instanceof DereferenceCodeP dereferenceCodeP)
            {
                ret.add(new DereferenceCodeP(lookupAddress(dereferenceCodeP.target()),lookupAddress(dereferenceCodeP.source()), dereferenceCodeP.size()));
            }
            if(code instanceof MoveCodeP moveCodeP)
            {
                ret.add(new MoveCodeP(lookupAddress(moveCodeP.target()),lookupAddress(moveCodeP.source()), moveCodeP.size()));
            }
        }
        return ret;
    }
    public static void emulateTAC(List<Code> codes)
    {
        Map<Integer,Integer> labelPos=new HashMap<>();
        for(int i=0;i<codes.size();i++)if(codes.get(i) instanceof LabelCode labelCode)labelPos.put(labelCode.id(),i);
        if(!variableMap.containsKey("main"))throw new DeclarationException("no main function defined");
        codes.add(new CallCodeP(lookupAddress(variableMap.get("main").first())));
        int ip=0;
        int bp=dataSegmentBaseAddress;
        int sp=bp;
        int ret=0;
        while(ip<codes.size())
        {
            Code code=codes.get(ip++);
            if(code instanceof LabelCode)continue;
            if(code instanceof UnconditionalGotoCode unconditionalGotoCode)
            {
                ip=labelPos.get(unconditionalGotoCode.id());
                continue;
            }
            if(code instanceof GotoCodeP gotoCode)
            {
                if((boolean) VirtualMemory.readFromMemory(addressTransformer(bp,gotoCode.left()),BOOL))ip=labelPos.get(gotoCode.id());
                continue;
            }
            if(code instanceof IndirectAssignCodeP indirectAssignCode)
            {
                VirtualMemory.moveBytes((int) VirtualMemory.readFromMemory(addressTransformer(bp,indirectAssignCode.target()),INT),addressTransformer(bp,indirectAssignCode.source()),indirectAssignCode.size());
                continue;
            }
            if(code instanceof ReferenceCode referenceCode)
            {
                VirtualMemory.writeToMemory(addressTransformer(bp,referenceCode.target()),addressTransformer(bp,referenceCode.left()));
                continue;
            }
            if(code instanceof AssignMMCodeP assignMMCodeP)
            {
                VirtualMemory.writeToMemory(addressTransformer(bp,assignMMCodeP.target()),assignMMCodeP.op().operation.cal(VirtualMemory.readFromMemory(addressTransformer(bp,assignMMCodeP.left()),assignMMCodeP.type()), VirtualMemory.readFromMemory(addressTransformer(bp,assignMMCodeP.right()),assignMMCodeP.type()),assignMMCodeP.type()));
                continue;
            }
            if(code instanceof AssignMICodeP assignMICodeP)
            {
                VirtualMemory.writeToMemory(addressTransformer(bp,assignMICodeP.target()),assignMICodeP.op().operation.cal(VirtualMemory.readFromMemory(addressTransformer(bp,assignMICodeP.left()),assignMICodeP.type()), assignMICodeP.right(),assignMICodeP.type()));
                continue;
            }
            if(code instanceof CastMMCodeP castMMCodeP)
            {
                VirtualMemory.writeToMemory(addressTransformer(bp,castMMCodeP.target()),Util.castPrimitiveType(VirtualMemory.readFromMemory(addressTransformer(bp, castMMCodeP.source()), castMMCodeP.sourceType()), castMMCodeP.targetType(), castMMCodeP.sourceType()));
                continue;
            }
            if(code instanceof ReturnCode returnCode)
            {
                ret=addressTransformer(bp,returnCode.val());
                sp=bp+2*ADDRESS_WIDTH+returnCode.size();
                ip=(int) VirtualMemory.readFromMemory(bp+ADDRESS_WIDTH,INT);
                bp=(int) VirtualMemory.readFromMemory(bp,INT);
                continue;
            }
            if(code instanceof CallCodeP callCode)
            {
                int callTarget= (int) VirtualMemory.readFromMemory(addressTransformer(bp,callCode.target()),INT);
                VirtualMemory.writeToMemory(sp-=ADDRESS_WIDTH,ip);
                VirtualMemory.writeToMemory(sp-=ADDRESS_WIDTH,bp);
                bp=sp;
                sp=bp-localVariableSize.get(callTarget);
                ip=labelPos.get(callTarget);
                continue;
            }
            if(code instanceof PushCodeP pushCodeP)
            {
                VirtualMemory.moveBytes(sp-=pushCodeP.size(),addressTransformer(bp,pushCodeP.source()),pushCodeP.size());
                continue;
            }
            if(code instanceof FetchReturnValueCodeP fetchReturnValueCode)
            {
                VirtualMemory.moveBytes(addressTransformer(bp,fetchReturnValueCode.target()),ret,fetchReturnValueCode.size());
                continue;
            }
            if(code instanceof DereferenceCodeP dereferenceCodeP)
            {
                VirtualMemory.moveBytes(addressTransformer(bp,dereferenceCodeP.target()),(int) VirtualMemory.readFromMemory(addressTransformer(bp, dereferenceCodeP.source()),INT),dereferenceCodeP.size());
                continue;
            }
            if(code instanceof MoveCodeP moveCodeP)
            {
                VirtualMemory.moveBytes(addressTransformer(bp,moveCodeP.target()),addressTransformer(bp, moveCodeP.source()),moveCodeP.size());
                continue;
            }
            System.out.println("Unknown TAC: "+code.toString());
        }
        printResult();
    }
    private static void printVariable(int id,Type type)
    {
        if(type instanceof ArrayType arrayType)
        {
            System.out.print('{');
            for(int i=0;i<arrayType.size();i++)
            {
                printVariable(id+i*arrayType.baseType().getSize(),arrayType.baseType());
                if(i!=arrayType.size()-1)System.out.print(',');
            }
            System.out.print("}");
        }
        else
        {
            System.out.print(VirtualMemory.readFromMemory(id, Util.getPrimitiveType(type)));
        }
    }
    private static void printResult()
    {
        System.out.println("RESULT:");
        for(Map.Entry<String,Pair<Integer,Type>> entry:variableMap.entrySet())
        {
            if(entry.getValue().second() instanceof FunctionType)continue;
            System.out.print(entry.getKey()+"=");
            printVariable(lookupAddress(entry.getValue().first()),entry.getValue().second());
            System.out.println();
        }
    }
    public static void printCodeList(List<Code> codeList)
    {
        for(Code code:codeList)
        {
            if(code instanceof LabelCode labelCode&&labelBackMap.containsKey(labelCode.id())&&(labelMap.containsKey(labelBackMap.get(labelCode.id()))))ctx=labelCode.id();
            if(code instanceof ReturnCode)ctx=-1;
            System.out.println(code);
        }
    }
    public static void main(String[] args) throws IOException
    {
        List<AbstractToken> abstractTokenList =lexer(Path.of(args[0]));
        abstractTokenList=tokenParser(abstractTokenList);
        abstractTokenList=preprocessor(abstractTokenList);
        List<Code> codeList=parser(abstractTokenList);
        codeList=typeEraser(codeList);
        printCodeList(codeList);
        codeList=assignAddress(codeList);
        emulateTAC(codeList);
    }
}

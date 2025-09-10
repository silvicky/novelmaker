package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.code.*;
import io.silvicky.novel.compiler.code.primitive.AssignMICodeP;
import io.silvicky.novel.compiler.code.primitive.AssignMMCodeP;
import io.silvicky.novel.compiler.code.primitive.CastMMCodeP;
import io.silvicky.novel.compiler.code.raw.*;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.Program;
import io.silvicky.novel.compiler.parser.operation.Skip;
import io.silvicky.novel.compiler.parser.operation.Operation;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.*;
import io.silvicky.novel.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static io.silvicky.novel.compiler.types.PrimitiveType.BOOL;
import static io.silvicky.novel.compiler.types.PrimitiveType.INT;
import static io.silvicky.novel.compiler.types.Type.ADDRESS_TYPE;
import static io.silvicky.novel.util.Util.addNonNull;

public class Compiler
{
    private static int labelCnt=0;
    private static int variableCnt=0;
    public static int ctx=-1;
    private static final long[] mem=new long[1048576];
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
        int ret=variableCnt+dataSegmentBaseAddress;
        variableMap.put(s,new Pair<>(ret,type));
        variableBackMap.put(ret,new Pair<>(s,type));
        variableCnt+=type.getSize();
        return ret;
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
        localVariableCount.put(ctx,cnt+type.getSize());
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
    public static String lookupLabelName(int l)
    {
        if(!labelBackMap.containsKey(l))return "L"+l;
        return labelBackMap.get(l);
    }
    public static int requestLabel(){return labelCnt++;}
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
        root.travel();
        ctx=-1;
        for(int i=0;i<root.codes.size();i++)
        {
            if(root.codes.get(i) instanceof LabelCode labelCode)
            {
                if(!labelBackMap.containsKey(labelCode.id()))continue;
                if(labelBackMap.get(labelCode.id()).charAt(0)=='0')ctx=-1;
                else ctx=labelCode.id();
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
    public static long toLong(Object o)
    {
        //TODO Remove this
        if(o instanceof Integer integer)return integer;
        if(o instanceof Long lon)return lon;
        if(o instanceof Boolean bool)return bool?1:0;
        if(o instanceof Character cha)return cha;
        if(o instanceof Short sho)return sho;
        throw new RuntimeException();
    }
    public static PrimitiveType getPrimitiveType(Type type)
    {
        while(type instanceof ConstType constType)type=constType.baseType();
        if(type instanceof PointerType||type instanceof ArrayType||type instanceof FunctionType)
        {
            return ADDRESS_TYPE;
        }
        if(type instanceof PrimitiveType primitiveType)return primitiveType;
        throw new GrammarException("Unknown type");
    }
    public static List<Code> typeEraser(List<Code> codes)
    {
        List<Code> ret=new ArrayList<>();
        ctx=-1;
        for(Code code:codes)
        {
            if(code instanceof LabelCode labelCode)
            {
                if(!labelBackMap.containsKey(labelCode.id()))continue;
                if(labelBackMap.get(labelCode.id()).charAt(0)=='0')ctx=-1;
                else ctx=labelCode.id();
            }
            if(code instanceof AssignCode assignCode)
            {
                PrimitiveType targetType=getPrimitiveType(assignCode.targetType());
                Type ta= assignCode.leftType();
                Type tb= assignCode.rightType();
                while(ta instanceof ConstType ca)ta=ca.baseType();
                while(tb instanceof ConstType cb)tb=cb.baseType();
                int a=assignCode.left();
                int b=assignCode.right();
                int target=assignCode.target();
                switch(assignCode.op())
                {
                    case PLUS ->
                    {
                        if(ta instanceof ArrayType aa)ta=new PointerType(aa.baseType());
                        if(tb instanceof ArrayType ab)tb=new PointerType(ab.baseType());
                        if(ta instanceof FunctionType||tb instanceof FunctionType)throw new GrammarException("addition involving functions");
                        if(ta instanceof PointerType&&tb instanceof PointerType)throw new GrammarException("addition between pointers");
                        else if(tb instanceof PointerType pb)
                        {
                            int t1=requestInternalVariable();
                            ret.add(new AssignMICodeP(t1,a,pb.baseType().getSize(),ADDRESS_TYPE,OperatorType.MULTIPLY));
                            if(targetType!=ADDRESS_TYPE)
                            {
                                int t2 = requestInternalVariable();
                                ret.add(new AssignMMCodeP(t2, t1, b, ADDRESS_TYPE, OperatorType.PLUS));
                                ret.add(new CastMMCodeP(target,t2,targetType,ADDRESS_TYPE));
                            }
                            else
                            {
                                ret.add(new AssignMMCodeP(target,t1,b,ADDRESS_TYPE,OperatorType.PLUS));
                            }
                        }
                        else if(ta instanceof PointerType pa)
                        {
                            int t1=requestInternalVariable();
                            ret.add(new AssignMICodeP(t1,b,pa.baseType().getSize(),ADDRESS_TYPE,OperatorType.MULTIPLY));
                            if(targetType!=ADDRESS_TYPE)
                            {
                                int t2 = requestInternalVariable();
                                ret.add(new AssignMMCodeP(t2, t1, a, ADDRESS_TYPE, OperatorType.PLUS));
                                ret.add(new CastMMCodeP(target,t2,targetType,ADDRESS_TYPE));
                            }
                            else
                            {
                                ret.add(new AssignMMCodeP(target,t1,a,ADDRESS_TYPE,OperatorType.PLUS));
                            }
                        }
                        else
                        {
                            if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
                            PrimitiveType type=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
                            if(!pa.equals(type))
                            {
                                int t1=requestInternalVariable();
                                ret.add(new CastMMCodeP(t1,a,type,pa));
                                a=t1;
                            }
                            if(!pb.equals(type))
                            {
                                int t1=requestInternalVariable();
                                ret.add(new CastMMCodeP(t1,b,type,pb));
                                b=t1;
                            }
                            if(type.equals(targetType))
                            {
                                ret.add(new AssignMMCodeP(target,a,b,type,OperatorType.PLUS));
                            }
                            else
                            {
                                int t1=requestInternalVariable();
                                ret.add(new AssignMMCodeP(t1,a,b,type,OperatorType.PLUS));
                                ret.add(new CastMMCodeP(target,t1,targetType,type));
                            }
                        }
                    }
                    case MINUS ->
                    {
                        if(ta instanceof FunctionType||tb instanceof FunctionType)throw new GrammarException("addition involving functions");
                        if(ta instanceof ArrayType aa)ta=new PointerType(aa.baseType());
                        if(tb instanceof ArrayType ab)tb=new PointerType(ab.baseType());
                        if(ta instanceof PointerType pa&&tb instanceof PointerType pb)
                        {
                            if(!pa.equals(pb))throw new GrammarException("minus between different pointers");
                            int t1=requestInternalVariable();
                            ret.add(new AssignMMCodeP(t1,a,b,ADDRESS_TYPE,OperatorType.MINUS));
                            if(targetType!=ADDRESS_TYPE)
                            {
                                int t2 = requestInternalVariable();
                                ret.add(new AssignMICodeP(t2, t1, pb.baseType().getSize(), ADDRESS_TYPE, OperatorType.DIVIDE));
                                ret.add(new CastMMCodeP(target,t2,targetType,ADDRESS_TYPE));
                            }
                            else
                            {
                                ret.add(new AssignMICodeP(target,t1,pb.baseType().getSize(),ADDRESS_TYPE,OperatorType.DIVIDE));
                            }
                        }
                        else if(tb instanceof PointerType)
                        {
                            throw new GrammarException("number minus pointer");
                        }
                        else if(ta instanceof PointerType pa)
                        {
                            int t1=requestInternalVariable();
                            ret.add(new AssignMICodeP(t1,b,pa.baseType().getSize(),ADDRESS_TYPE,OperatorType.MULTIPLY));
                            if(targetType!=ADDRESS_TYPE)
                            {
                                int t2 = requestInternalVariable();
                                ret.add(new AssignMICodeP(t2, a, t1, ADDRESS_TYPE, OperatorType.MINUS));
                                ret.add(new CastMMCodeP(target,t2,targetType,ADDRESS_TYPE));
                            }
                            else
                            {
                                ret.add(new AssignMICodeP(target,a,t1,ADDRESS_TYPE,OperatorType.MINUS));
                            }
                        }
                        else
                        {
                            if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
                            PrimitiveType type=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
                            if(!pa.equals(type))
                            {
                                int t1=requestInternalVariable();
                                ret.add(new CastMMCodeP(t1,a,type,pa));
                                a=t1;
                            }
                            if(!pb.equals(type))
                            {
                                int t1=requestInternalVariable();
                                ret.add(new CastMMCodeP(t1,b,type,pb));
                                b=t1;
                            }
                            if(type.equals(targetType))
                            {
                                ret.add(new AssignMMCodeP(target,a,b,type,OperatorType.MINUS));
                            }
                            else
                            {
                                int t1=requestInternalVariable();
                                ret.add(new AssignMMCodeP(t1,a,b,type,OperatorType.MINUS));
                                ret.add(new CastMMCodeP(target,t1,targetType,type));
                            }
                        }
                    }
                    case L_SHIFT,R_SHIFT,MOD,AND,OR,XOR, MULTIPLY,DIVIDE ->
                    {
                        PrimitiveType pa=getPrimitiveType(ta);
                        PrimitiveType pb=getPrimitiveType(tb);
                        if((assignCode.op().equals(OperatorType.L_SHIFT)
                                ||assignCode.op().equals(OperatorType.R_SHIFT)
                                ||assignCode.op().equals(OperatorType.MOD)
                                ||assignCode.op().equals(OperatorType.AND)
                                ||assignCode.op().equals(OperatorType.OR)
                                ||assignCode.op().equals(OperatorType.XOR))&&(!(pa.isInteger()&&pb.isInteger())))throw new GrammarException("not integer");
                        PrimitiveType type=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
                        if(!pa.equals(type))
                        {
                            int t1=requestInternalVariable();
                            ret.add(new CastMMCodeP(t1,a,type,pa));
                            a=t1;
                        }
                        if(!pb.equals(type))
                        {
                            int t1=requestInternalVariable();
                            ret.add(new CastMMCodeP(t1,b,type,pb));
                            b=t1;
                        }
                        if(type.equals(targetType))
                        {
                            ret.add(new AssignMMCodeP(target,a,b,type,assignCode.op()));
                        }
                        else
                        {
                            int t1=requestInternalVariable();
                            ret.add(new AssignMMCodeP(t1,a,b,type,assignCode.op()));
                            ret.add(new CastMMCodeP(target,t1,targetType,type));
                        }
                    }
                    case NOP-> ret.add(new CastMMCodeP(target,a,targetType,getPrimitiveType(ta)));
                    case COMMA -> ret.add(new CastMMCodeP(target,b,targetType,getPrimitiveType(tb)));
                    case REVERSE ->
                    {
                        PrimitiveType pa=getPrimitiveType(ta);
                        if(!(pa.isInteger()))throw new GrammarException("not integer");
                        if(pa.equals(targetType))
                        {
                            ret.add(new AssignMMCodeP(target,a,a,pa,OperatorType.REVERSE));
                        }
                        else
                        {
                            int t1=requestInternalVariable();
                            ret.add(new AssignMMCodeP(t1,a,a,pa,OperatorType.REVERSE));
                            ret.add(new CastMMCodeP(target,t1,targetType,pa));
                        }
                    }
                    case NOT ->
                    {
                        PrimitiveType pa=getPrimitiveType(ta);
                        if(targetType.equals(BOOL))
                        {
                            ret.add(new AssignMMCodeP(target,a,a,pa,OperatorType.NOT));
                        }
                        else
                        {
                            int t1=requestInternalVariable();
                            ret.add(new AssignMMCodeP(t1,a,a,BOOL,OperatorType.NOT));
                            ret.add(new CastMMCodeP(target,t1,targetType,BOOL));
                        }
                    }
                    case LESS,GREATER,LESS_EQUAL,GREATER_EQUAL,EQUAL_EQUAL,NOT_EQUAL ->
                    {
                        PrimitiveType pa=getPrimitiveType(ta);
                        PrimitiveType pb=getPrimitiveType(tb);
                        PrimitiveType type=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
                        if(!pa.equals(type))
                        {
                            int t1=requestInternalVariable();
                            ret.add(new CastMMCodeP(t1,a,type,pa));
                            a=t1;
                        }
                        if(!pb.equals(type))
                        {
                            int t1=requestInternalVariable();
                            ret.add(new CastMMCodeP(t1,b,type,pb));
                            b=t1;
                        }
                        if(BOOL.equals(targetType))
                        {
                            ret.add(new AssignMMCodeP(target,a,b,type,assignCode.op()));
                        }
                        else
                        {
                            int t1=requestInternalVariable();
                            ret.add(new AssignMMCodeP(t1,a,b,type,assignCode.op()));
                            ret.add(new CastMMCodeP(target,t1,targetType,BOOL));
                        }
                    }
                    default -> throw new GrammarException("Unknown operation");
                }
            }
            else
            {
                ret.add(code);
            }
        }
        return ret;
    }
    public static void emulateTAC(List<Code> codes)
    {
        Map<Integer,Integer> labelPos=new HashMap<>();
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
                //TODO all below
                if(gotoCode.op().operation.cal(mem[addressTransformer(bp,gotoCode.left())],mem[addressTransformer(bp,gotoCode.right())],INT,INT).second()!=0)ip=labelPos.get(gotoCode.id());
                continue;
            }
            if(code instanceof AssignNumberCode assignNumberCode)
            {
                mem[addressTransformer(bp,assignNumberCode.target())]= toLong(assignNumberCode.left());
                continue;
            }
            if(code instanceof IndirectAssignCode indirectAssignCode)
            {
                //mem[addressTransformer(bp,(int)mem[addressTransformer(bp,indirectAssignCode.target())])]=mem[addressTransformer(bp,indirectAssignCode.left())];
                mem[(int)mem[addressTransformer(bp,indirectAssignCode.target())]]=mem[addressTransformer(bp,indirectAssignCode.left())];
                continue;
            }
            if(code instanceof ReferenceCode referenceCode)
            {
                mem[addressTransformer(bp,referenceCode.target())]=addressTransformer(bp,referenceCode.left());
                continue;
            }
            if(code instanceof AssignCode assignCode)
            {
                mem[addressTransformer(bp,assignCode.target())]=assignCode.op().operation.cal(mem[addressTransformer(bp,assignCode.left())],mem[addressTransformer(bp,assignCode.right())],assignCode.leftType(),assignCode.rightType()).second();
                continue;
            }
            if(code instanceof AssignVariableNumberCode assignVariableNumberCode)
            {
                mem[addressTransformer(bp,assignVariableNumberCode.target())]=assignVariableNumberCode.op().operation.cal(mem[addressTransformer(bp,assignVariableNumberCode.left())], (Integer) assignVariableNumberCode.right(),assignVariableNumberCode.leftType(),assignVariableNumberCode.rightType()).second();
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
            if(code instanceof DereferenceCode dereferenceCode)
            {
                if(dereferenceCode.type() instanceof FunctionType)mem[addressTransformer(bp,dereferenceCode.target())]=mem[addressTransformer(bp, dereferenceCode.left())];
                else mem[addressTransformer(bp,dereferenceCode.target())]=mem[(int) mem[addressTransformer(bp, dereferenceCode.left())]];
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
            System.out.print(mem[id]);
        }
    }
    private static void printResult()
    {
        System.out.println("RESULT:");
        for(Map.Entry<String,Pair<Integer,Type>> entry:variableMap.entrySet())
        {
            if(entry.getValue().second() instanceof FunctionType)continue;
            System.out.print(entry.getKey()+"=");
            printVariable(entry.getValue().first(),entry.getValue().second());
            System.out.println();
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
        List<Code> erasedCodeList=typeEraser(codeList);
        emulateTAC(erasedCodeList);
    }
}

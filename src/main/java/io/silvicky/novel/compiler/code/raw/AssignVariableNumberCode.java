package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.AssignMICodeP;
import io.silvicky.novel.compiler.code.primitive.AssignMMCodeP;
import io.silvicky.novel.compiler.code.primitive.CastMMCodeP;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.*;
import io.silvicky.novel.util.Util;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;
import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.compiler.tokens.OperatorType.COMMA;
import static io.silvicky.novel.compiler.types.PrimitiveType.BOOL;
import static io.silvicky.novel.compiler.types.PrimitiveType.VOID;
import static io.silvicky.novel.compiler.types.Type.ADDRESS_TYPE;

public record AssignVariableNumberCode(int target, int left, Object right, Type targetType, Type leftType, Type rightType,OperatorType op) implements RawCode
{
    public String toString()
    {
        if(op.properties== OperatorType.OperatorProperties.BINARY)return lookupVariableName(target)+"="+lookupVariableName(left)+op.symbol+right;
        else return lookupVariableName(target)+"="+op.symbol+lookupVariableName(left);
    }

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        //TODO in fact some are impossible
        PrimitiveType targetType= Util.getPrimitiveType(this.targetType());
        Type ta= this.leftType();
        Type tb= this.rightType();
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if((op!=COMMA)&&ta==VOID)throw new GrammarException("using void value");
        int a=this.left();
        Object b=this.right();
        int target=this.target();
        switch(this.op())
        {
            case PLUS ->
            {
                if(ta instanceof ArrayType aa)ta=new PointerType(aa.baseType());
                if(tb instanceof ArrayType ab)tb=new PointerType(ab.baseType());
                if(ta instanceof FunctionType||tb instanceof FunctionType)throw new GrammarException("addition involving functions");
                if(ta instanceof PointerType&&tb instanceof PointerType)throw new GrammarException("addition between pointers");
                else if(tb instanceof PointerType pb)
                {
                    if((!(ta instanceof PrimitiveType pa))||!pa.isInteger())throw new GrammarException("addition between pointer and float");
                    if(pa!=ADDRESS_TYPE)
                    {
                        int t1=requestInternalVariable(ADDRESS_TYPE);
                        ret.add(new CastMMCodeP(t1,a,ADDRESS_TYPE,pa));
                        a=t1;
                    }
                    int t1=requestInternalVariable(ADDRESS_TYPE);
                    ret.add(new AssignMICodeP(t1,a,pb.baseType().getSize(),ADDRESS_TYPE,OperatorType.MULTIPLY));
                    if(targetType!=ADDRESS_TYPE)
                    {
                        int t2 = requestInternalVariable(ADDRESS_TYPE);
                        ret.add(new AssignMICodeP(t2, t1, b, ADDRESS_TYPE, OperatorType.PLUS));
                        ret.add(new CastMMCodeP(target,t2,targetType,ADDRESS_TYPE));
                    }
                    else
                    {
                        ret.add(new AssignMICodeP(target,t1,b,ADDRESS_TYPE,OperatorType.PLUS));
                    }
                }
                else if(ta instanceof PointerType pa)
                {
                    if((!(tb instanceof PrimitiveType pb))||!pb.isInteger())throw new GrammarException("addition between pointer and float");
                    if(pb!=ADDRESS_TYPE)b= Util.castPrimitiveType(b,ADDRESS_TYPE,pb);
                    int t1=((int)b)*pa.baseType().getSize();
                    if(targetType!=ADDRESS_TYPE)
                    {
                        int t2 = requestInternalVariable(ADDRESS_TYPE);
                        ret.add(new AssignMICodeP(t2, a, t1, ADDRESS_TYPE, OperatorType.PLUS));
                        ret.add(new CastMMCodeP(target,t2,targetType,ADDRESS_TYPE));
                    }
                    else
                    {
                        ret.add(new AssignMICodeP(target,a,t1,ADDRESS_TYPE,OperatorType.PLUS));
                    }
                }
                else
                {
                    if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
                    PrimitiveType type=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
                    if(!pa.equals(type))
                    {
                        int t1=requestInternalVariable(type);
                        ret.add(new CastMMCodeP(t1,a,type,pa));
                        a=t1;
                    }
                    if(!pb.equals(type))b= Util.castPrimitiveType(b,type,pb);
                    if(type.equals(targetType))
                    {
                        ret.add(new AssignMICodeP(target,a,b,type,OperatorType.PLUS));
                    }
                    else
                    {
                        int t1=requestInternalVariable(type);
                        ret.add(new AssignMICodeP(t1,a,b,type,OperatorType.PLUS));
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
                    int t1=requestInternalVariable(ADDRESS_TYPE);
                    ret.add(new AssignMICodeP(t1,a,b,ADDRESS_TYPE,OperatorType.MINUS));
                    if(targetType!=ADDRESS_TYPE)
                    {
                        int t2 = requestInternalVariable(ADDRESS_TYPE);
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
                    if((!(tb instanceof PrimitiveType pb))||!pb.isInteger())throw new GrammarException("addition between pointer and float");
                    if(pb!=ADDRESS_TYPE)b= Util.castPrimitiveType(b,ADDRESS_TYPE,pb);
                    int t1=((int)b)*pa.baseType().getSize();
                    if(targetType!=ADDRESS_TYPE)
                    {
                        int t2 = requestInternalVariable(ADDRESS_TYPE);
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
                        int t1=requestInternalVariable(type);
                        ret.add(new CastMMCodeP(t1,a,type,pa));
                        a=t1;
                    }
                    if(!pb.equals(type))b= Util.castPrimitiveType(b,type,pb);
                    if(type.equals(targetType))
                    {
                        ret.add(new AssignMICodeP(target,a,b,type,OperatorType.MINUS));
                    }
                    else
                    {
                        int t1=requestInternalVariable(type);
                        ret.add(new AssignMICodeP(t1,a,b,type,OperatorType.MINUS));
                        ret.add(new CastMMCodeP(target,t1,targetType,type));
                    }
                }
            }
            case L_SHIFT,R_SHIFT,MOD,AND,OR,XOR, MULTIPLY,DIVIDE ->
            {
                PrimitiveType pa= Util.getPrimitiveType(ta);
                PrimitiveType pb= Util.getPrimitiveType(tb);
                if((this.op().equals(OperatorType.L_SHIFT)
                        ||this.op().equals(OperatorType.R_SHIFT)
                        ||this.op().equals(OperatorType.MOD)
                        ||this.op().equals(OperatorType.AND)
                        ||this.op().equals(OperatorType.OR)
                        ||this.op().equals(OperatorType.XOR))&&(!(pa.isInteger()&&pb.isInteger())))throw new GrammarException("not integer");
                PrimitiveType type=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
                if(!pa.equals(type))
                {
                    int t1=requestInternalVariable(type);
                    ret.add(new CastMMCodeP(t1,a,type,pa));
                    a=t1;
                }
                if(!pb.equals(type))b= Util.castPrimitiveType(b,type,pb);
                if(type.equals(targetType))
                {
                    ret.add(new AssignMICodeP(target,a,b,type,this.op()));
                }
                else
                {
                    int t1=requestInternalVariable(type);
                    ret.add(new AssignMICodeP(t1,a,b,type,this.op()));
                    ret.add(new CastMMCodeP(target,t1,targetType,type));
                }
            }
            case NOP-> ret.add(new CastMMCodeP(target,a,targetType, Util.getPrimitiveType(ta)));
            case COMMA -> ret.add(new AssignMICodeP(target,0, Util.castPrimitiveType(b,targetType, Util.getPrimitiveType(tb)),targetType,OperatorType.COMMA));
            case REVERSE ->
            {
                PrimitiveType pa= Util.getPrimitiveType(ta);
                if(!(pa.isInteger()))throw new GrammarException("not integer");
                if(pa.equals(targetType))
                {
                    ret.add(new AssignMMCodeP(target,a,a,pa,OperatorType.REVERSE));
                }
                else
                {
                    int t1=requestInternalVariable(pa);
                    ret.add(new AssignMMCodeP(t1,a,a,pa,OperatorType.REVERSE));
                    ret.add(new CastMMCodeP(target,t1,targetType,pa));
                }
            }
            case NOT ->
            {
                PrimitiveType pa= Util.getPrimitiveType(ta);
                if(targetType.equals(BOOL))
                {
                    ret.add(new AssignMMCodeP(target,a,a,pa,OperatorType.NOT));
                }
                else
                {
                    int t1=requestInternalVariable(BOOL);
                    ret.add(new AssignMMCodeP(t1,a,a,BOOL,OperatorType.NOT));
                    ret.add(new CastMMCodeP(target,t1,targetType,BOOL));
                }
            }
            case LESS,GREATER,LESS_EQUAL,GREATER_EQUAL,EQUAL_EQUAL,NOT_EQUAL ->
            {
                PrimitiveType pa= Util.getPrimitiveType(ta);
                PrimitiveType pb= Util.getPrimitiveType(tb);
                PrimitiveType type=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
                if(!pa.equals(type))
                {
                    int t1=requestInternalVariable(type);
                    ret.add(new CastMMCodeP(t1,a,type,pa));
                    a=t1;
                }
                if(!pb.equals(type))b= Util.castPrimitiveType(b,type,pb);
                if(BOOL.equals(targetType))
                {
                    ret.add(new AssignMICodeP(target,a,b,type,this.op()));
                }
                else
                {
                    int t1=requestInternalVariable(BOOL);
                    ret.add(new AssignMICodeP(t1,a,b,type,this.op()));
                    ret.add(new CastMMCodeP(target,t1,targetType,BOOL));
                }
            }
            default -> throw new GrammarException("Unknown operation");
        }
        return ret;
    }
}
package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.AssignMICodeP;
import io.silvicky.novel.compiler.code.primitive.AssignMMCodeP;
import io.silvicky.novel.compiler.code.primitive.CastMMCodeP;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.*;
import static io.silvicky.novel.compiler.tokens.OperatorType.*;
import static io.silvicky.novel.compiler.types.PrimitiveType.BOOL;
import static io.silvicky.novel.compiler.types.Type.ADDRESS_TYPE;

public record AssignCode(int target, int left, int right, Type targetType, Type leftType, Type rightType, OperatorType op) implements RawCode
{
    public String toString()
    {
        if(op.properties== OperatorType.OperatorArgsProperties.BINARY)return lookupVariableName(target)+"="+lookupVariableName(left)+op.symbol+lookupVariableName(right);
        else return lookupVariableName(target)+"="+op.symbol+lookupVariableName(left);
    }

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        PrimitiveType targetType=getPrimitiveType(this.targetType());
        Type ta= this.leftType();
        Type tb= this.rightType();
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        int a=this.left();
        int b=this.right();
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
                        int t1=requestInternalVariable();
                        ret.add(new CastMMCodeP(t1,a,ADDRESS_TYPE,pa));
                        a=t1;
                    }
                    int t1=requestInternalVariable();
                    ret.add(new AssignMICodeP(t1,a,pb.baseType().getSize(),ADDRESS_TYPE,OperatorType.MULTIPLY));
                    if(targetType!=ADDRESS_TYPE)
                    {
                        int t2 = requestInternalVariable();
                        ret.add(new AssignMMCodeP(t2, t1, b, ADDRESS_TYPE, PLUS));
                        ret.add(new CastMMCodeP(target,t2,targetType,ADDRESS_TYPE));
                    }
                    else
                    {
                        ret.add(new AssignMMCodeP(target,t1,b,ADDRESS_TYPE, PLUS));
                    }
                }
                else if(ta instanceof PointerType pa)
                {
                    if((!(tb instanceof PrimitiveType pb))||!pb.isInteger())throw new GrammarException("addition between pointer and float");
                    if(pb!=ADDRESS_TYPE)
                    {
                        int t1=requestInternalVariable();
                        ret.add(new CastMMCodeP(t1,b,ADDRESS_TYPE,pb));
                        b=t1;
                    }
                    int t1=requestInternalVariable();
                    ret.add(new AssignMICodeP(t1,b,pa.baseType().getSize(),ADDRESS_TYPE,OperatorType.MULTIPLY));
                    if(targetType!=ADDRESS_TYPE)
                    {
                        int t2 = requestInternalVariable();
                        ret.add(new AssignMMCodeP(t2, t1, a, ADDRESS_TYPE, PLUS));
                        ret.add(new CastMMCodeP(target,t2,targetType,ADDRESS_TYPE));
                    }
                    else
                    {
                        ret.add(new AssignMMCodeP(target,t1,a,ADDRESS_TYPE, PLUS));
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
                        ret.add(new AssignMMCodeP(target,a,b,type, PLUS));
                    }
                    else
                    {
                        int t1=requestInternalVariable();
                        ret.add(new AssignMMCodeP(t1,a,b,type, PLUS));
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
                    if((!(tb instanceof PrimitiveType pb))||!pb.isInteger())throw new GrammarException("addition between pointer and float");
                    if(pb!=ADDRESS_TYPE)
                    {
                        int t1=requestInternalVariable();
                        ret.add(new CastMMCodeP(t1,b,ADDRESS_TYPE,pb));
                        b=t1;
                    }
                    int t1=requestInternalVariable();
                    ret.add(new AssignMICodeP(t1,b,pa.baseType().getSize(),ADDRESS_TYPE,OperatorType.MULTIPLY));
                    if(targetType!=ADDRESS_TYPE)
                    {
                        int t2 = requestInternalVariable();
                        ret.add(new AssignMMCodeP(t2, a, t1, ADDRESS_TYPE, OperatorType.MINUS));
                        ret.add(new CastMMCodeP(target,t2,targetType,ADDRESS_TYPE));
                    }
                    else
                    {
                        ret.add(new AssignMMCodeP(target,a,t1,ADDRESS_TYPE,OperatorType.MINUS));
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
                if((this.op().equals(OperatorType.L_SHIFT)
                        ||this.op().equals(OperatorType.R_SHIFT)
                        ||this.op().equals(OperatorType.MOD)
                        ||this.op().equals(OperatorType.AND)
                        ||this.op().equals(OperatorType.OR)
                        ||this.op().equals(OperatorType.XOR))&&(!(pa.isInteger()&&pb.isInteger())))throw new GrammarException("not integer");
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
                    ret.add(new AssignMMCodeP(target,a,b,type,this.op()));
                }
                else
                {
                    int t1=requestInternalVariable();
                    ret.add(new AssignMMCodeP(t1,a,b,type,this.op()));
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
                    ret.add(new AssignMMCodeP(target,a,b,type,this.op()));
                }
                else
                {
                    int t1=requestInternalVariable();
                    ret.add(new AssignMMCodeP(t1,a,b,type,this.op()));
                    ret.add(new CastMMCodeP(target,t1,targetType,BOOL));
                }
            }
            default -> throw new GrammarException("Unknown operation");
        }
        return ret;
    }
}

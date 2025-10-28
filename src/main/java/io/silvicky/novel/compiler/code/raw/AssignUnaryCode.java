package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.AssignMCodeP;
import io.silvicky.novel.compiler.code.primitive.CastCodeP;
import io.silvicky.novel.compiler.code.primitive.MoveCodeP;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.ConstType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Util;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;
import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.compiler.tokens.OperatorType.NOP;
import static io.silvicky.novel.compiler.types.PrimitiveType.BOOL;

public record AssignUnaryCode(int target, int left, Type targetType, Type leftType, OperatorType op) implements RawCode
{
    public String toString()
    {
        return lookupVariableName(target)+"="+op.symbol+lookupVariableName(left);
    }

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        Type ta= this.leftType();
        while(ta instanceof ConstType ca)ta=ca.baseType();
        Type tt= this.targetType;
        while(tt instanceof ConstType ct)tt=ct.baseType();
        if(op==NOP&&ta.equals(tt))
        {
            ret.add(new MoveCodeP(target,left,Util.getPrimitiveSize(tt)));
            return ret;
        }
        int a=this.left();
        int target=this.target();
        PrimitiveType targetType= Util.getPrimitiveType(this.targetType());
        switch(this.op())
        {
            case NOP-> ret.add(new CastCodeP(target,a,targetType, Util.getPrimitiveType(ta)));
            case REVERSE ->
            {
                PrimitiveType pa= Util.getPrimitiveType(ta);
                if(!(pa.isInteger()))throw new GrammarException("not integer");
                if(pa.equals(targetType))
                {
                    ret.add(new AssignMCodeP(target,a,pa,OperatorType.REVERSE));
                }
                else
                {
                    int t1=requestInternalVariable(pa);
                    ret.add(new AssignMCodeP(t1,a,pa,OperatorType.REVERSE));
                    ret.add(new CastCodeP(target,t1,targetType,pa));
                }
            }
            case NOT ->
            {
                PrimitiveType pa= Util.getPrimitiveType(ta);
                if(targetType.equals(BOOL))
                {
                    ret.add(new AssignMCodeP(target,a,pa,OperatorType.NOT));
                }
                else
                {
                    int t1=requestInternalVariable(BOOL);
                    ret.add(new AssignMCodeP(t1,a,pa,OperatorType.NOT));
                    ret.add(new CastCodeP(target,t1,targetType,BOOL));
                }
            }
            default -> throw new GrammarException("Unknown operation");
        }
        return ret;
    }
}

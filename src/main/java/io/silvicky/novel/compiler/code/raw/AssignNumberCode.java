package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.AssignMICodeP;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.*;

public record AssignNumberCode(int target, Object left, Type targetType,Type leftType) implements RawCode
{
    public String toString()
    {
        return lookupVariableName(target)+"="+left;
    }

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        PrimitiveType pt=getPrimitiveType(targetType);
        if(!(leftType instanceof PrimitiveType pl))return null;
        ret.add(new AssignMICodeP(target,0,castPrimitiveType(left,pt,pl),pt,OperatorType.COMMA));
        return ret;
    }
}

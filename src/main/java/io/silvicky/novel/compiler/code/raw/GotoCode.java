package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.AssignMCodeP;
import io.silvicky.novel.compiler.code.primitive.CastCodeP;
import io.silvicky.novel.compiler.code.primitive.GotoCodeP;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Util;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.*;

public record GotoCode(int left, Type type, boolean isReversed, int id) implements RawCode
{
    @Override
    public String toString()
    {
        if(isReversed)return String.format("if !%s goto %s",
                lookupVariableName(left),
                lookupLabelName(id));
        else return String.format("if %s goto %s",
                lookupVariableName(left),
                lookupLabelName(id));
    }

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        PrimitiveType primitiveType= Util.getPrimitiveType(type);
        int t1=requestInternalVariable(PrimitiveType.BOOL);
        if(isReversed)
        {
            ret.add(new AssignMCodeP(t1,left,primitiveType, OperatorType.NOT));
        }
        else
        {
            ret.add(new CastCodeP(t1,left,PrimitiveType.BOOL,primitiveType));
        }
        ret.add(new GotoCodeP(t1,id));
        return ret;
    }
}

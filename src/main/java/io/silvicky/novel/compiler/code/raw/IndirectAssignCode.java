package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.IndirectAssignCodeP;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record IndirectAssignCode(int target, int left, Type targetType) implements RawCode
{
    public String toString()
    {
        return "*"+lookupVariableName(target)+"="+lookupVariableName(left);
    }

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        ret.add(new IndirectAssignCodeP(target,left,targetType.getSize()));
        return ret;
    }
}

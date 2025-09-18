package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.FetchReturnValueCodeP;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record FetchReturnValueCode(int target, Type type) implements RawCode
{
    @Override
    public String toString(){return lookupVariableName(target)+"=RET";}

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        ret.add(new FetchReturnValueCodeP(target, type.getSize()));
        return ret;
    }
}

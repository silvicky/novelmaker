package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.CallCodeP;
import io.silvicky.novel.compiler.code.primitive.PushCodeP;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record CallCode(int target, List<Integer> parameters, List<Type> args) implements RawCode
{
    @Override
    public String toString(){return String.format("%s(%s)",lookupVariableName(target),parameters.toString());}

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        for(int i= parameters.size()-1;i>=0;i--)
        {
            ret.add(new PushCodeP(parameters.get(i),args.get(i).getSize()));
        }
        ret.add(new CallCodeP(target));
        return ret;
    }
}

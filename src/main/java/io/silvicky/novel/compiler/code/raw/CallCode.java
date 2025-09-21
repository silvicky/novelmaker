package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.CallCodeP;
import io.silvicky.novel.compiler.code.primitive.PushCodeP;
import io.silvicky.novel.compiler.code.primitive.PopCodeP;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record CallCode(int target, List<Integer> args, List<Type> params) implements RawCode
{
    @Override
    public String toString(){return String.format("%s(%s)",lookupVariableName(target), args.toString());}

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        for(int i = args.size()-1; i>=0; i--)
        {
            ret.add(new PushCodeP(args.get(i), params.get(i).getSize()));
        }
        ret.add(new CallCodeP(target));
        for(int i = 0; i< args.size(); i++)
        {
            ret.add(new PopCodeP(params.get(i).getSize()));
        }
        return ret;
    }
}

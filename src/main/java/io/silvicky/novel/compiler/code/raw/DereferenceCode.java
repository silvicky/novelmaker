package io.silvicky.novel.compiler.code.raw;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.code.primitive.DereferenceCodeP;
import io.silvicky.novel.compiler.code.primitive.MoveCodeP;
import io.silvicky.novel.compiler.types.AbstractPointer;
import io.silvicky.novel.compiler.types.FunctionType;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;
import static io.silvicky.novel.compiler.types.Type.ADDRESS_WIDTH;

public record DereferenceCode(int target, int left, Type type) implements RawCode
{
    @Override
    public String toString()
    {
        return String.format("%s=*(%s)",lookupVariableName(target),lookupVariableName(left));
    }

    @Override
    public List<Code> analyze()
    {
        List<Code> ret=new ArrayList<>();
        if(type instanceof FunctionType)ret.add(new MoveCodeP(target,left,ADDRESS_WIDTH));
        else ret.add(new DereferenceCodeP(target,left,((AbstractPointer)type).baseType().getSize()));
        return ret;
    }
}

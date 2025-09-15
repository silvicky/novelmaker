package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.types.Type;

import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record CallCode(int target, List<Integer> parameters, List<Type> args) implements Code
{
    @Override
    public String toString(){return String.format("%s(%s)",lookupVariableName(target),parameters.toString());}
}

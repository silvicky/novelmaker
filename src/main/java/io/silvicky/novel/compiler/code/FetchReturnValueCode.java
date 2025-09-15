package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.types.Type;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record FetchReturnValueCode(int target, Type type) implements Code
{
    @Override
    public String toString(){return lookupVariableName(target)+"=RET";}
}

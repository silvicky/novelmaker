package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.types.Type;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record ReturnCode(int val, Type type) implements Code
{
    @Override
    public String toString(){return "RET "+lookupVariableName(val);}
}

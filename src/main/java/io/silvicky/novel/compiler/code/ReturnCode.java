package io.silvicky.novel.compiler.code;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record ReturnCode(int val,int size) implements Code
{
    @Override
    public String toString(){return "RET "+lookupVariableName(val);}
}

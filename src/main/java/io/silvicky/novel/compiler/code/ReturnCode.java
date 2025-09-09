package io.silvicky.novel.compiler.code;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record ReturnCode(int val) implements Code
{
    @Override
    public String toString(){return "RET "+lookupVariableName(val);}
}

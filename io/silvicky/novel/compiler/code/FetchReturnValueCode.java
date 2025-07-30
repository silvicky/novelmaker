package io.silvicky.novel.compiler.code;

import static io.silvicky.novel.compiler.Compiler.lookupVariableName;

public record FetchReturnValueCode(int target) implements Code
{
    @Override
    public String toString(){return lookupVariableName(target)+"=RET";}
}

package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.tokens.OperatorType;

import static io.silvicky.novel.compiler.Compiler.lookupLabelName;

public record UnconditionalGotoCode(int id) implements Code
{
    @Override
    public String toString()
    {
        return "goto "+lookupLabelName(id);
    }
}

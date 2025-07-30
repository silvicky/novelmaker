package io.silvicky.novel.compiler.code;

import static io.silvicky.novel.compiler.Compiler.lookupLabelName;

public record PlaceholderUnconditionalGotoCode(int ctx, String labelName) implements Code
{
    @Override
    public String toString()
    {
        return String.format("In function %s goto label %s", lookupLabelName(ctx),labelName);
    }
}

package io.silvicky.novel.compiler.parser.operation;

import static io.silvicky.novel.compiler.Compiler.ctx;
public record ResetCtxOperation() implements Operation
{
    @Override
    public void execute()
    {
        ctx=-1;
    }
}

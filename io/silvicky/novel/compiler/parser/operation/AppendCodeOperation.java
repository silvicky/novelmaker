package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.code.Code;

public record AppendCodeOperation(NonTerminal target, Code source) implements Operation
{
    @Override
    public void execute()
    {
        target.codes.add(source);
    }
}

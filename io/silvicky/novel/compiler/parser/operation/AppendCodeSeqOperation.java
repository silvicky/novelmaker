package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;

public record AppendCodeSeqOperation(NonTerminal target, AbstractToken source) implements Operation
{
    @Override
    public void execute()
    {
        if(!(source instanceof NonTerminal))return;
        target.codes.addAll(((NonTerminal) source).codes);
    }
}

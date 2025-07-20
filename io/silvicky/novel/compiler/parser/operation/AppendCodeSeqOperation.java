package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.Token;

public record AppendCodeSeqOperation(NonTerminal target, Token source) implements Operation
{
    @Override
    public void execute()
    {
        if(!(source instanceof NonTerminal))return;
        target.codes.addAll(((NonTerminal) source).codes);
    }
}

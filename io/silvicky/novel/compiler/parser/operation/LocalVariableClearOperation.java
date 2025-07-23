package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.parser.NonTerminal;

import static io.silvicky.novel.compiler.Compiler.revokeLocalVariable;

public record LocalVariableClearOperation(NonTerminal nonTerminal) implements Operation
{
    @Override
    public void execute()
    {
        for(String i:nonTerminal.revokedVariables)revokeLocalVariable(i);
    }
}

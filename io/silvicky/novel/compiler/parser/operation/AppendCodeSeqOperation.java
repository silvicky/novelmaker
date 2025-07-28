package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.ASTNode;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.AbstractToken;

public record AppendCodeSeqOperation(NonTerminal target, AbstractToken source) implements Operation
{
    @Override
    public void execute()
    {
        if(!(source instanceof NonTerminal))return;
        if(source instanceof ASTNode node)node.travel();
        target.codes.addAll(((NonTerminal) source).codes);
    }
}

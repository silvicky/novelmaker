package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.parser.expression.AbstractExpressionResidue;

public record ResolveOperation(AbstractExpressionResidue<?> residue) implements Operation
{
    @Override
    public void execute()
    {
        residue.resolve();
    }
}

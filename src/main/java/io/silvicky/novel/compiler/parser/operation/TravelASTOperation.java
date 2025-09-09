package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.parser.ASTNode;

public record TravelASTOperation(ASTNode astNode) implements Operation
{
    @Override
    public void execute()
    {
        astNode.travel();
    }
}


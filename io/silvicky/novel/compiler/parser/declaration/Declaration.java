package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.BaseTypeBuilderRoot;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

public class Declaration extends NonTerminal implements ASTNode
{
    private final BaseTypeBuilderRoot baseTypeBuilderRoot;
    private AssignmentDeclaration assignmentDeclaration;
    public Declaration child=null;
    public final NonTerminal directParent;

    public Declaration(BaseTypeBuilderRoot baseTypeBuilderRoot, NonTerminal directParent)
    {
        this.baseTypeBuilderRoot = baseTypeBuilderRoot;
        this.directParent = directParent;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        assignmentDeclaration =new AssignmentDeclaration(baseTypeBuilderRoot,directParent);
        DeclarationResidue declarationResidue =new DeclarationResidue(this,baseTypeBuilderRoot,directParent);
        ret.add(new ResolveOperation(declarationResidue));
        ret.add(declarationResidue);
        ret.add(assignmentDeclaration);
        return ret;
    }

    @Override
    public void travel()
    {
        assignmentDeclaration.travel();
        child.travel();
        codes.addAll(assignmentDeclaration.codes);
        if(child!=null)codes.addAll(child.codes);
    }
}

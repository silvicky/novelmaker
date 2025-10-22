package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

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
        if(next instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.SEMICOLON)
        {
            ret.add(new OperatorToken(OperatorType.SEMICOLON));
            return ret;
        }
        if(baseTypeBuilderRoot.structDeclaration==null&&baseTypeBuilderRoot.keywordTypeList.isEmpty())throw new GrammarException("not a declaration");
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
        if(assignmentDeclaration==null)return;
        assignmentDeclaration.travel();
        codes.addAll(assignmentDeclaration.codes);
        if(child!=null)
        {
            child.travel();
            codes.addAll(child.codes);
        }
    }
}

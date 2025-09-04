package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.BaseTypeBuilderRoot;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

public class DeclarationRoot extends NonTerminal implements ASTNode
{
    private BaseTypeBuilderRoot baseTypeBuilderRoot;
    private Declaration declaration;
    public final NonTerminal directParent;
    private final boolean isAbstract;

    public DeclarationRoot(NonTerminal directParent, boolean isAbstract)
    {
        this.directParent = directParent;
        this.isAbstract = isAbstract;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        baseTypeBuilderRoot = new BaseTypeBuilderRoot();
        declaration =new Declaration(baseTypeBuilderRoot,directParent);
        ret.add(declaration);
        ret.add(baseTypeBuilderRoot);
        return ret;
    }

    @Override
    public void travel()
    {
        baseTypeBuilderRoot.travel();
        declaration.travel();
        codes.addAll(declaration.codes);
    }
}

package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.BaseTypeBuilderRoot;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Argument extends NonTerminal implements ASTNode
{
    private BaseTypeBuilderRoot baseTypeBuilderRoot;
    private UnaryDeclaration declaration;
    public final DeclarationPostfix directParent;
    private final boolean isAbstract;
    public String name;
    public Type type;

    public Argument(DeclarationPostfix directParent, boolean isAbstract)
    {
        this.directParent = directParent;
        this.isAbstract = isAbstract;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        baseTypeBuilderRoot = new BaseTypeBuilderRoot();
        declaration =new UnaryDeclaration(baseTypeBuilderRoot);
        ret.add(declaration);
        ret.add(baseTypeBuilderRoot);
        return ret;
    }

    @Override
    public void travel()
    {
        baseTypeBuilderRoot.travel();
        declaration.travel();
        name= declaration.name;
        type= declaration.type;
        directParent.parameters.add(new Pair<>(type,name));
    }
}

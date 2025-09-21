package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.operation.Skip;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Argument extends NonTerminal implements ASTNode
{
    private BaseTypeBuilderRoot baseTypeBuilderRoot;
    private UnaryDeclaration declaration;
    public final DeclarationPostfix directParent;
    public String name;
    public Type type;
    private boolean isEllipsis=false;

    public Argument(DeclarationPostfix directParent)
    {
        this.directParent = directParent;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.ELLIPSIS)
        {
            isEllipsis=true;
            ret.add(new Skip());
            ret.add(new OperatorToken(OperatorType.ELLIPSIS));
            return ret;
        }
        baseTypeBuilderRoot = new BaseTypeBuilderRoot();
        declaration =new UnaryDeclaration();
        ret.add(declaration);
        ret.add(baseTypeBuilderRoot);
        return ret;
    }

    @Override
    public void travel()
    {
        if(isEllipsis)
        {
            directParent.parameters.add(new Pair<>(PrimitiveType.ELLIPSIS,""));
            return;
        }
        baseTypeBuilderRoot.travel();
        declaration.receivedType= baseTypeBuilderRoot.type;
        declaration.travel();
        name= declaration.name;
        type= declaration.type;
        directParent.parameters.add(new Pair<>(type,name));
    }
}

package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

public class PrimaryDeclaration extends NonTerminal implements ASTNode
{
    private final BaseTypeBuilderRoot baseTypeBuilderRoot;
    public String name;
    public Type receivedType;
    public Type type;
    private UnaryDeclaration nextExpression=null;

    public PrimaryDeclaration(BaseTypeBuilderRoot baseTypeBuilderRoot)
    {
        this.baseTypeBuilderRoot = baseTypeBuilderRoot;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken)
        {
            name=identifierToken.id;
            ret.add(new IdentifierToken(identifierToken.id));
            return ret;
        }
        nextExpression=new UnaryDeclaration(baseTypeBuilderRoot);
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(nextExpression);
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        return ret;
    }

    @Override
    public void travel()
    {
        if(nextExpression!=null)
        {
            nextExpression.receivedType=receivedType;
            nextExpression.travel();
            name=nextExpression.name;
            type= nextExpression.type;
            return;
        }
        type= receivedType;
    }
}

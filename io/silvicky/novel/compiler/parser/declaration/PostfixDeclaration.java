package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.BaseTypeBuilderRoot;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.ArrayType;
import io.silvicky.novel.compiler.types.FunctionType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PostfixDeclaration extends NonTerminal implements ASTNode
{

    private final BaseTypeBuilderRoot baseTypeBuilderRoot;
    public final List<DeclarationPostfix> postfixes=new ArrayList<>();
    private PrimaryDeclaration nextExpression;
    public List<Pair<Type,String>> parameters;
    public String name;
    public Type type;

    public PostfixDeclaration(BaseTypeBuilderRoot baseTypeBuilderRoot)
    {
        this.baseTypeBuilderRoot = baseTypeBuilderRoot;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        nextExpression=new PrimaryDeclaration(baseTypeBuilderRoot);
        ret.add(new DeclarationPostfixes(this));
        ret.add(nextExpression);
        return ret;
    }
    @Override
    public void travel()
    {
        nextExpression.travel();
        name=nextExpression.name;
        type= nextExpression.type;
        for(DeclarationPostfix postfix:postfixes.reversed())
        {
            if(postfix.operatorType==OperatorType.L_PARENTHESES)
            {
                parameters=postfix.parameters;
                type=new FunctionType(type,postfix.parameters.stream().map(Pair::first).toList());
            }
            else
            {
                type=new ArrayType(type,postfix.size);
            }
        }
    }
}

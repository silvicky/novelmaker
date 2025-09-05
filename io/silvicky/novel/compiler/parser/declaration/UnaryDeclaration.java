package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.ConstType;
import io.silvicky.novel.compiler.types.PointerType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class UnaryDeclaration extends NonTerminal implements ASTNode
{
    private UnaryDeclaration child=null;
    private boolean isPointer=false;
    private boolean isConst=false;
    private PostfixDeclaration postfixDeclaration=null;
    private final BaseTypeBuilderRoot baseTypeBuilderRoot;
    public List<Pair<Type,String>> parameters;
    public String name;
    public Type receivedType;
    public Type type;

    public UnaryDeclaration(BaseTypeBuilderRoot baseTypeBuilderRoot)
    {
        this.baseTypeBuilderRoot = baseTypeBuilderRoot;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.MULTIPLY)
        {
            child=new UnaryDeclaration(baseTypeBuilderRoot);
            isPointer=true;
            ret.add(child);
            ret.add(new OperatorToken(OperatorType.MULTIPLY));
            return ret;
        }
        if(next instanceof KeywordToken keywordToken&&keywordToken.type== KeywordType.CONST)
        {
            child=new UnaryDeclaration(baseTypeBuilderRoot);
            isConst=true;
            ret.add(child);
            ret.add(new KeywordToken(KeywordType.CONST));
            return ret;
        }
        postfixDeclaration=new PostfixDeclaration(baseTypeBuilderRoot);
        ret.add(postfixDeclaration);
        return ret;
    }
    @Override
    public void travel()
    {
        while(child!=null)
        {
            isConst=isConst||child.isConst;
            isPointer=isPointer||child.isPointer;
            postfixDeclaration=child.postfixDeclaration;
            child=child.child;
        }
        if(isPointer)
        {
            receivedType=new PointerType(receivedType);
            if(isConst)receivedType=new ConstType(receivedType);
        }
        postfixDeclaration.receivedType=receivedType;
        postfixDeclaration.travel();
        name= postfixDeclaration.name;
        type= postfixDeclaration.type;
        parameters= postfixDeclaration.parameters;
    }
}

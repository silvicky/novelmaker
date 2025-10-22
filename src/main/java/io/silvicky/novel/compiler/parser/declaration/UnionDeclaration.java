package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.UnionType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.*;

public class UnionDeclaration extends NonTerminal implements ASTNode
{
    public String name=null;
    private DeclarationBlock declarationBlock=null;
    public UnionType unionType=null;
    @Override
    public void travel()
    {
        if(declarationBlock!=null)
        {
            declarationBlock.travel();
            unionType=new UnionType(declarationBlock.fields);
            if(name!=null)
            {
                //TODO really?
                if(ctx==-1)registerUnion(name,unionType);
                else registerLocalUnion(name,unionType);
            }
        }
        else unionType=lookupUnion(name);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken)
        {
            if(second instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.L_BRACE)
            {
                declarationBlock=new DeclarationBlock();
                ret.add(declarationBlock);
            }
            name= identifierToken.id;
            ret.add(new IdentifierToken(name));
        }
        else
        {
            if(next instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.L_BRACE)
            {
                declarationBlock=new DeclarationBlock();
                ret.add(declarationBlock);
            }
            else throw new GrammarException("both name and definition are absent");
        }
        return ret;
    }
}
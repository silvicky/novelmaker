package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.BaseTypeBuilderRoot;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.AbstractExpressionResidue;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class DeclarationResidue extends AbstractExpressionResidue<Declaration>
{
    private Declaration declaration=null;
    private final BaseTypeBuilderRoot baseTypeBuilderRoot;
    public final NonTerminal directParent;
    protected DeclarationResidue(Declaration root, BaseTypeBuilderRoot baseTypeBuilderRoot, NonTerminal directParent)
    {
        super(root);
        this.baseTypeBuilderRoot=baseTypeBuilderRoot;
        this.directParent = directParent;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken)
        {
            if(operatorToken.type==OperatorType.SEMICOLON)
            {
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                return ret;
            }
            if(operatorToken.type== OperatorType.COMMA)
            {
                declaration=new Declaration(baseTypeBuilderRoot,directParent);
                ret.add(declaration);
                ret.add(new OperatorToken(OperatorType.COMMA));
            }
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.child=declaration;
    }
}

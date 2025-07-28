package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class VariableDeclarationResidue extends NonTerminal
{
    public final NonTerminal directParent;

    public VariableDeclarationResidue(NonTerminal directParent)
    {
        this.directParent = directParent;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(!(next instanceof OperatorToken operatorToken))throw new GrammarException(this.getClass().getSimpleName()+next+second);
        if(operatorToken.type== OperatorType.COMMA)
        {
            VariableDeclaration declaration=new VariableDeclaration(this.directParent);
            ret.add(new AppendCodeSeqOperation(this,declaration));
            ret.add(declaration);
            ret.add(new OperatorToken(OperatorType.COMMA));
        }
        return ret;
    }
}

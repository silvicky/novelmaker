package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.operation.TravelASTOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class Arguments extends NonTerminal
{
    private final DeclarationPostfix postfix;

    public Arguments(DeclarationPostfix postfix)
    {
        this.postfix = postfix;
    }


    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.R_PARENTHESES)return ret;
        ret.add(new ArgumentsResidue(this,postfix));
        Argument argument=new Argument(postfix, false);
        ret.add(new TravelASTOperation(argument));
        ret.add(argument);
        return ret;
    }
}

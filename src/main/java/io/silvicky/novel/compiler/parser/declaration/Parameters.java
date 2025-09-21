package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.operation.TravelASTOperation;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Parameters extends NonTerminal
{
    private final DeclarationPostfix postfix;

    public Parameters(DeclarationPostfix postfix)
    {
        this.postfix = postfix;
    }


    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.R_PARENTHESES)return ret;
        if(next instanceof KeywordToken keywordToken&&keywordToken.type== KeywordType.VOID)
        {
            ret.add(new KeywordToken(KeywordType.VOID));
            return ret;
        }
        Parameter parameter =new Parameter(postfix);
        ret.add(new TravelASTOperation(parameter));
        ret.add(new ParametersResidue(this,postfix));
        ret.add(parameter);
        return ret;
    }
}

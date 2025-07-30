package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class ArgumentsResidue extends NonTerminal
{
    private final int functionId;

    public ArgumentsResidue(int functionId)
    {
        this.functionId = functionId;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.R_PARENTHESES)return ret;
        ret.add(new Arguments(functionId));
        ret.add(new OperatorToken(OperatorType.COMMA));
        return ret;
    }
}

package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.code.AssignNumberCode;
import io.silvicky.novel.compiler.parser.operation.AppendCodeOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.NumberToken;

import java.util.ArrayList;
import java.util.List;

public class NumberValue extends NonTerminal
{
    private final int target;

    public NumberValue(int target)
    {
        this.target = target;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(!(next instanceof NumberToken numberToken))throw new GrammarException("init value not constant");
        ret.add(new AppendCodeOperation(this,new AssignNumberCode(target,numberToken.value)));
        ret.add(new NumberToken(numberToken.value));
        return ret;
    }
}

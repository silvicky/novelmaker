package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.code.AssignNumberCode;
import io.silvicky.novel.compiler.parser.operation.AppendCodeOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.NumberToken;
import io.silvicky.novel.compiler.types.Type;

import java.util.ArrayList;
import java.util.List;

public class NumberValue extends NonTerminal
{
    private final int target;
    private final Type type;

    public NumberValue(int target, Type type)
    {
        this.target = target;
        this.type = type;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(!(next instanceof NumberToken<?> numberToken))throw new GrammarException("init value not constant");
        ret.add(new AppendCodeOperation(this,new AssignNumberCode(target,numberToken.value, type,numberToken.type)));
        ret.add(new NumberToken<>());
        return ret;
    }
}

package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.NumberToken;

import java.util.ArrayList;
import java.util.List;

public class NumberEntityResidue implements AbstractJsonEntity
{
    private final NumberEntity root;

    public NumberEntityResidue(NumberEntity root)
    {
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        root.value=(Number) (((NumberToken<?>)next).value);
        ret.add(next);
        return ret;
    }
}

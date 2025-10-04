package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NullEntity implements JsonEntity
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new IdentifierToken("null"));
        return ret;
    }

    @Override
    public Object adapt(Type type)
    {
        return null;
    }
}

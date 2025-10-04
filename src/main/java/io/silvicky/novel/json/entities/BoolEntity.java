package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BoolEntity implements JsonEntity
{
    private final boolean value;

    public BoolEntity(boolean value)
    {
        this.value = value;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new IdentifierToken(value?"true":"false"));
        return ret;
    }

    @Override
    public Object adapt(Type type)
    {
        if(type.equals(Boolean.class))return value;
        throw new RuntimeException("not a Boolean");
    }
}

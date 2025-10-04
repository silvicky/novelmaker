package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.StringToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StringEntity implements JsonEntity
{
    private final String content;

    public StringEntity(String content)
    {
        this.content = content;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new StringToken(content));
        return ret;
    }

    @Override
    public Object adapt(Type type)
    {
        if(type.equals(String.class))return content;
        throw new RuntimeException("not a String");
    }
}

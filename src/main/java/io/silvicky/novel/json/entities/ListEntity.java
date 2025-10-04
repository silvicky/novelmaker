package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListEntity implements JsonEntity
{
    public final List<JsonEntity> list=new ArrayList<>();
    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new OperatorToken(OperatorType.R_BRACKET));
        ret.add(new ListContent(this));
        ret.add(new OperatorToken(OperatorType.L_BRACKET));
        return ret;
    }

    @Override
    public Object adapt(Type type)
    {
        try
        {
            ParameterizedType parameterizedType=(ParameterizedType)type;
            Constructor<?> constructor=((Class<?>)parameterizedType.getRawType()).getDeclaredConstructor();
            constructor.setAccessible(true);
            Collection<Object> result = (Collection<Object>) constructor.newInstance();
            Type parameterType=parameterizedType.getActualTypeArguments()[0];
            for(JsonEntity jsonEntity:list)
            {
                result.add(jsonEntity.adapt(parameterType));
            }
            return result;
        }
        catch (Exception e)
        {
            throw new RuntimeException("not a Collection");
        }
    }
}

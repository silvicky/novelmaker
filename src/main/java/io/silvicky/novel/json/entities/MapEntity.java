package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapEntity implements JsonEntity
{
    public final Map<String,JsonEntity> map=new HashMap<>();
    @Override
    public Object adapt(Type type)
    {
        if(type instanceof ParameterizedType parameterizedType&&Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType()))
        {
            try
            {
                Map<String,Object> result = (Map<String,Object>) ((Class<?>)parameterizedType.getRawType()).getDeclaredConstructor().newInstance();
                Class<?> clazz2 = (Class<?>)parameterizedType.getActualTypeArguments()[1];
                for (Map.Entry<String, JsonEntity> entry : map.entrySet())
                {
                    result.put(entry.getKey(),entry.getValue().adapt(clazz2));
                }
                return result;
            }
            catch (Exception e)
            {
                throw new RuntimeException("not a Map");
            }
        }
        else
        {
            try
            {
                Class<?> clazz=(type instanceof ParameterizedType parameterizedType)
                        ?(Class<?>) parameterizedType.getRawType()
                        :(Class<?>) type;
                Constructor<?> constructor=clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                Object result = constructor.newInstance();
                for (Map.Entry<String, JsonEntity> entry : map.entrySet())
                {
                    Field field= clazz.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    field.set(result,entry.getValue().adapt(field.getGenericType()));
                }
                return result;
            }
            catch (Exception e)
            {
                throw new RuntimeException("failed to access members");
            }
        }
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new OperatorToken(OperatorType.R_BRACE));
        ret.add(new MapContent(this));
        ret.add(new OperatorToken(OperatorType.L_BRACE));
        return ret;
    }
}

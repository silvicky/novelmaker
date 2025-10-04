package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.util.Util.findJsonEntity;

public class MapValue implements AbstractJsonEntity
{
    private final String key;
    private final MapEntity root;

    public MapValue(String key, MapEntity root)
    {
        this.key = key;
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        JsonEntity jsonEntity=findJsonEntity(next);
        ret.add(jsonEntity);
        root.map.put(key,jsonEntity);
        return ret;
    }
}

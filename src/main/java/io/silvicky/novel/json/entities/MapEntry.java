package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class MapEntry implements AbstractJsonEntity
{
    private final MapEntity root;

    public MapEntry(MapEntity root)
    {
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        String key=((StringToken)next).content;
        ret.add(new MapValue(key,root));
        ret.add(new OperatorToken(OperatorType.COLON));
        ret.add(new StringToken(key));
        return ret;
    }
}

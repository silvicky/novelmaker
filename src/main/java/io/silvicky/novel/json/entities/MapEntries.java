package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

public class MapEntries implements AbstractJsonEntity
{
    private final MapEntity root;

    public MapEntries(MapEntity root)
    {
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new MapEntriesResidue(root));
        ret.add(new MapEntry(root));
        return ret;
    }
}

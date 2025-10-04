package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.util.Util.findJsonEntity;

public class ListEntries implements AbstractJsonEntity
{
    private final ListEntity root;

    public ListEntries(ListEntity root)
    {
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new ListEntriesResidue(root));
        JsonEntity jsonEntity=findJsonEntity(next);
        ret.add(jsonEntity);
        root.list.add(jsonEntity);
        return ret;
    }
}

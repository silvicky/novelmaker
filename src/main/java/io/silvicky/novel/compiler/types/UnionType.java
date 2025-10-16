package io.silvicky.novel.compiler.types;

import io.silvicky.novel.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnionType implements CompoundType
{
    private final Map<String,Type> members=new HashMap<>();
    private final int size;

    public UnionType(List<Pair<String, Type>> members)
    {
        int maxSize=0;
        for(Pair<String,Type> pr:members)
        {
            if(this.members.containsKey(pr.first()))throw new RuntimeException("repeated members");
            this.members.put(pr.first(), pr.second());
            maxSize=Math.max(maxSize,pr.second().getSize());
        }
        size=maxSize;
    }

    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public boolean isAuto()
    {
        return false;
    }

    @Override
    public Pair<Type, Integer> lookupMember(String name)
    {
        return new Pair<>(members.get(name), 0);
    }
}

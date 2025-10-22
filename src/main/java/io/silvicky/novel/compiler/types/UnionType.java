package io.silvicky.novel.compiler.types;

import io.silvicky.novel.util.Pair;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UnionType implements CompoundType,Iterable<Map.Entry<String,Type>>
{
    private final LinkedHashMap<String,Type> members=new LinkedHashMap<>();
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

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Map.Entry<String, Type>> iterator()
    {
        return members.entrySet().iterator();
    }
}

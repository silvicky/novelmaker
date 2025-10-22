package io.silvicky.novel.compiler.types;

import io.silvicky.novel.util.Pair;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StructType implements CompoundType, Iterable<Map.Entry<String,Pair<Type,Integer>>>
{
    private final LinkedHashMap<String,Pair<Type,Integer>> members=new LinkedHashMap<>();
    private final int size;

    public StructType(List<Pair<String, Type>> members)
    {
        int curSize=0;
        for(Pair<String,Type> pr:members)
        {
            if(this.members.containsKey(pr.first()))throw new RuntimeException("repeated members");
            this.members.put(pr.first(), new Pair<>(pr.second(),curSize));
            curSize+=pr.second().getSize();
        }
        size=curSize;
    }

    @Override
    public Pair<Type, Integer> lookupMember(String name)
    {
        return members.get(name);
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

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Map.Entry<String, Pair<Type, Integer>>> iterator()
    {
        return members.entrySet().iterator();
    }
}

package io.silvicky.novel.compiler.types;

import io.silvicky.novel.util.Pair;

public interface CompoundType extends Type
{
    Pair<Type,Integer> lookupMember(String name);
}

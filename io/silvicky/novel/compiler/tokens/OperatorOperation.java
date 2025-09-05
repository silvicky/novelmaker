package io.silvicky.novel.compiler.tokens;

import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

public interface OperatorOperation
{
    Pair<Type,Long> cal(long a, long b, Type aType, Type bType);
}

package io.silvicky.novel.compiler.tokens;

import io.silvicky.novel.compiler.types.PrimitiveType;

public interface OperatorOperation
{
    Object cal(Object a, Object b, PrimitiveType type);
}

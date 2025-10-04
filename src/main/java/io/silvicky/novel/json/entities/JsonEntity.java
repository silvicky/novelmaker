package io.silvicky.novel.json.entities;

import java.lang.reflect.Type;

public interface JsonEntity extends AbstractJsonEntity
{
    Object adapt(Type type);
}

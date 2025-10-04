package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.List;

public interface AbstractJsonEntity extends AbstractToken
{
    List<AbstractToken> lookup(AbstractToken next);
}

package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.tokens.AbstractToken;
public interface Operation extends AbstractToken
{
    void execute();
}

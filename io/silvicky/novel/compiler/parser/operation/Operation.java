package io.silvicky.novel.compiler.parser.operation;

import io.silvicky.novel.compiler.tokens.Token;
public interface Operation extends Token
{
    void execute();
}

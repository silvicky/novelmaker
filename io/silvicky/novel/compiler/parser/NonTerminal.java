package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.Token;

import java.util.List;

public interface NonTerminal extends Token
{
    List<Token> lookup(Token next, Token second) throws GrammarException;
}

package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public abstract class NonTerminal implements Token
{
    public List<Code> codes=new ArrayList<>();
    public abstract List<Token> lookup(Token next, Token second);
}

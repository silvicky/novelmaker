package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.code.Code;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

public abstract class NonTerminal implements AbstractToken
{
    public final List<Code> codes=new ArrayList<>();
    public final List<String> revokedVariables=new ArrayList<>();
    public abstract List<AbstractToken> lookup(AbstractToken next, AbstractToken second);
}

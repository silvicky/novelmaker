package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class ContinueLine extends NonTerminal implements ASTNode
{
    private final int continueLabel;

    public ContinueLine(int continueLabel)
    {
        this.continueLabel = continueLabel;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new OperatorToken(OperatorType.SEMICOLON));
        ret.add(new KeywordToken(KeywordType.CONTINUE));
        return ret;
    }

    @Override
    public void travel()
    {
        codes.add(new UnconditionalGotoCode(continueLabel));
    }
}

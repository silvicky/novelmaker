package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestLabel;

public class DefaultLine extends NonTerminal implements ASTNode
{
    private final SwitchLine root;

    public DefaultLine(SwitchLine root)
    {
        this.root = root;
    }

    @Override
    public void travel()
    {
        int id=requestLabel();
        codes.add(new LabelCode(id));
        root.defaultCase=id;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new OperatorToken(OperatorType.COLON));
        ret.add(new KeywordToken(KeywordType.DEFAULT));
        return ret;
    }
}

package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.registerLocalLabel;

public class LabelLine extends NonTerminal implements ASTNode
{
    private final String id;

    public LabelLine(String id)
    {
        this.id = id;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new OperatorToken(OperatorType.LABEL));
        ret.add(new IdentifierToken(id));
        return ret;
    }

    @Override
    public void travel()
    {
        codes.add(new LabelCode(registerLocalLabel(id)));
    }
}

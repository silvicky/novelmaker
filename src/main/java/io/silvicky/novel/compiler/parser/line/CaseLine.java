package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestLabel;

public class CaseLine extends NonTerminal implements ASTNode
{
    private final SwitchLine root;
    ExpressionRoot expression;

    public CaseLine(SwitchLine root)
    {
        this.root = root;
    }

    @Override
    public void travel()
    {
        int id=requestLabel();
        codes.add(new LabelCode(id));
        root.cases.add(new Pair<>(expression,id));
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new OperatorToken(OperatorType.COLON));
        expression=new ExpressionRoot();
        ret.add(expression);
        ret.add(new KeywordToken(KeywordType.CASE));
        return ret;
    }
}

package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.GotoCode;
import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class IfLine extends NonTerminal implements ASTNode
{
    public final int breakLabel,continueLabel;
    LabelCode end;
    Line line;
    ExpressionRoot expression;
    Else els;
    LabelCode elseLabel;

    public IfLine(int breakLabel, int continueLabel)
    {
        this.breakLabel = breakLabel;
        this.continueLabel = continueLabel;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        end=new LabelCode();
        line=new Line(breakLabel,continueLabel,null);
        expression=new ExpressionRoot();
        els=new Else(breakLabel,continueLabel);
        elseLabel=new LabelCode();
        ret.add(els);
        ret.add(line);
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(expression);
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        ret.add(new KeywordToken(KeywordType.IF));
        return ret;
    }

    @Override
    public void travel()
    {
        expression.travel();
        codes.addAll(expression.codes);
        codes.add(new GotoCode(expression.resultId,0,OperatorType.NOT,elseLabel.id()));
        line.travel();
        codes.addAll(line.codes);
        codes.add(new UnconditionalGotoCode(end.id()));
        codes.add(elseLabel);
        els.travel();
        codes.addAll(els.codes);
        codes.add(end);
    }
}

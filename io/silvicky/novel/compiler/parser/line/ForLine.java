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

import static io.silvicky.novel.compiler.Compiler.revokeLocalVariable;

public class ForLine extends NonTerminal implements ASTNode
{
    ForFirst first;
    Line line;
    ExpressionRoot expression;
    ExpressionRoot third;
    LabelCode head;
    LabelCode cont;
    LabelCode end;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        head=new LabelCode();
        cont=new LabelCode();
        end=new LabelCode();
        line=new Line(end.id(),cont.id(),this);
        first=new ForFirst(this);
        expression=new ExpressionRoot();
        third=new ExpressionRoot();
        ret.add(line);
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(third);
        ret.add(new OperatorToken(OperatorType.SEMICOLON));
        ret.add(expression);
        ret.add(first);
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        ret.add(new KeywordToken(KeywordType.FOR));
        return ret;
    }
    @Override
    public void travel()
    {
        first.travel();
        codes.addAll(first.codes);
        codes.add(head);
        expression.travel();
        codes.addAll(expression.codes);
        codes.add(new GotoCode(expression.resultId,0,OperatorType.NOT, end.id()));
        line.travel();
        codes.addAll(line.codes);
        codes.add(cont);
        third.travel();
        codes.addAll(third.codes);
        codes.add(new UnconditionalGotoCode(head.id()));
        codes.add(end);
        for(String s:revokedVariables)revokeLocalVariable(s);
    }
}

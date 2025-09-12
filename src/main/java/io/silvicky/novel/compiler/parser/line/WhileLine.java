package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.raw.GotoCode;
import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.revokeLocalVariable;

public class WhileLine extends NonTerminal implements ASTNode
{
    LabelCode head;
    LabelCode end;
    Line line;
    ExpressionRoot expression;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        head=new LabelCode();
        end=new LabelCode();
        line=new Line(end.id(),head.id(),this);
        expression=new ExpressionRoot();
        ret.add(line);
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(expression);
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        ret.add(new KeywordToken(KeywordType.WHILE));
        return ret;
    }

    @Override
    public void travel()
    {
        codes.add(head);
        expression.travel();
        codes.addAll(expression.codes);
        codes.add(new GotoCode(expression.resultId,expression.type,true,end.id()));
        line.travel();
        codes.addAll(line.codes);
        codes.add(new UnconditionalGotoCode(head.id()));
        codes.add(end);
        for(String s:revokedVariables)revokeLocalVariable(s);
    }
}

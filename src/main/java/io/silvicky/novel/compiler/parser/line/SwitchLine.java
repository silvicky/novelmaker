package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.code.raw.GotoCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.compiler.Compiler.revokeLocalVariable;

public class SwitchLine extends NonTerminal implements ASTNode
{
    public List<Pair<ExpressionRoot,Integer>> cases=new ArrayList<>();
    public int defaultCase;
    LabelCode end;
    ExpressionRoot expression;
    Line line;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        end=new LabelCode();
        defaultCase=end.id();
        line=new Line(end.id(),-1,this);
        expression=new ExpressionRoot();
        ret.add(line);
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(expression);
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        ret.add(new KeywordToken(KeywordType.SWITCH));
        return ret;
    }
    @Override
    public void travel()
    {
        expression.travel();
        codes.addAll(expression.codes);
        line.travel();
        for(Pair<ExpressionRoot,Integer> pr:cases)
        {
            pr.first().travel();
            codes.addAll(pr.first().codes);
            int tmp=requestInternalVariable(PrimitiveType.BOOL);
            codes.add(new AssignCode(tmp,expression.resultId,pr.first().resultId,PrimitiveType.BOOL,expression.type,pr.first().type,OperatorType.EQUAL_EQUAL));
            codes.add(new GotoCode(tmp,PrimitiveType.BOOL,false,pr.second()));
        }
        codes.add(new UnconditionalGotoCode(defaultCase));
        codes.addAll(line.codes);
        codes.add(end);
        for(String s:revokedVariables)revokeLocalVariable(s);
    }
}

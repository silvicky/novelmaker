package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.code.GotoCode;
import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalLabel;
import static io.silvicky.novel.compiler.parser.expression.Rotator.rotateLeft;

public class ConditionalExpression extends AbstractExpression
{
    public LogicalOrExpression left;
    public ExpressionNew middle=null;
    public ConditionalExpression right=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new LogicalOrExpression();
        ret.add(new ConditionalExpressionResidue(this));
        ret.add(left);
        return ret;
    }

    @Override
    public void travel()
    {
        if(left.right instanceof LogicalOrExpression)left= rotateLeft(left);
        left.travel();
        codes.addAll(left.codes);
        if(middle!=null)
        {
            int lbRight=requestInternalLabel();
            int lbEnd=requestInternalLabel();
            codes.add(new GotoCode(left.resultId,-1, OperatorType.NOT,lbRight));
            if(middle.right instanceof ExpressionNew)middle= rotateLeft(middle);
            middle.travel();
            codes.addAll(middle.codes);
            codes.add(new AssignCode(resultId, middle.resultId,-1,OperatorType.NOP));
            codes.add(new UnconditionalGotoCode(lbEnd));
            codes.add(new LabelCode(lbRight));
            right.travel();
            codes.addAll(right.codes);
            codes.add(new AssignCode(resultId, right.resultId,-1,OperatorType.NOP));
            codes.add(new LabelCode(lbEnd));
        }
        else
        {
            codes.add(new AssignCode(resultId, left.resultId,-1,OperatorType.NOP));
        }
    }
}

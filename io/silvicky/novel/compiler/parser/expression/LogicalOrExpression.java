package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.parser.expression.Rotator.rotateLeft;

public class LogicalOrExpression extends LTRExpression
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new LogicalAndExpression();
        LogicalOrExpressionResidue residue=new LogicalOrExpressionResidue(this);
        ret.add(new ResolveOperation(residue));
        ret.add(residue);
        ret.add(left);
        return ret;
    }

    @Override
    public void travel()
    {
        if(left instanceof LogicalAndExpression left2&&left2.right instanceof LogicalAndExpression)left= rotateLeft(left2);
        left.travel();
        codes.addAll(left.codes);
        if(right!=null)
        {
            LogicalAndExpression right2=(LogicalAndExpression) right;
            if(right2.right instanceof LogicalAndExpression)right= rotateLeft(right2);
            right.travel();
            codes.addAll(right.codes);
            codes.add(new AssignCode(resultId,left.resultId,right.resultId, OperatorType.OR_OR));
        }
        else
        {
            codes.add(new AssignCode(resultId,left.resultId,left.resultId,OperatorType.NOP));
        }
    }
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.parser.expression.Rotator.rotateLeft;

public class ShiftExpression extends LTRExpression
{
    public OperatorType type=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new AdditiveExpression();
        ShiftExpressionResidue residue=new ShiftExpressionResidue(this);
        ret.add(new ResolveOperation(residue));
        ret.add(residue);
        ret.add(left);
        return ret;
    }

    @Override
    public void travel()
    {
        if(left instanceof AdditiveExpression left2&&left2.right instanceof AdditiveExpression)left= rotateLeft(left2);
        left.travel();
        codes.addAll(left.codes);
        if(right!=null)
        {
            AdditiveExpression right2=(AdditiveExpression) right;
            if(right2.right instanceof AdditiveExpression)right= rotateLeft(right2);
            right.travel();
            codes.addAll(right.codes);
            codes.add(new AssignCode(resultId,left.resultId,right.resultId, type));
        }
        else
        {
            codes.add(new AssignCode(resultId,left.resultId,left.resultId,OperatorType.NOP));
        }
    }
}

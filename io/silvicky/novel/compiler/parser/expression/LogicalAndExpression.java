package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.util.Util.getResultType;
import static io.silvicky.novel.util.Util.rotateLeft;

public class LogicalAndExpression extends LTRExpression
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new InclusiveOrExpression();
        LogicalAndExpressionResidue residue=new LogicalAndExpressionResidue(this);
        ret.add(new ResolveOperation(residue));
        ret.add(residue);
        ret.add(left);
        return ret;
    }

    @Override
    public void travel()
    {
        if(left instanceof InclusiveOrExpression left2&&left2.right instanceof InclusiveOrExpression)left= rotateLeft(left2);
        left.travel();
        codes.addAll(left.codes);
        if(right!=null)
        {
            InclusiveOrExpression right2=(InclusiveOrExpression) right;
            if(right2.right instanceof InclusiveOrExpression)right= rotateLeft(right2);
            right.travel();
            codes.addAll(right.codes);
            type=getResultType(left.type,right.type,OperatorType.AND_AND);
            leftId=-1;
            codes.add(new AssignCode(resultId,left.resultId,right.resultId,type,left.type,right.type, OperatorType.AND_AND));
        }
        else
        {
            type=left.type;
            leftId=left.leftId;
            isDirect=left.isDirect;
            codes.add(new AssignCode(resultId,left.resultId,left.resultId,type,type,type,OperatorType.NOP));
        }
    }
}

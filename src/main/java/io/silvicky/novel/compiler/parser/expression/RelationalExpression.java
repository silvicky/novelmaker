package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.util.Util.rotateLeft;

public class RelationalExpression extends LTRExpression
{
    public OperatorType op =null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new ShiftExpression();
        RelationalExpressionResidue residue=new RelationalExpressionResidue(this);
        ret.add(new ResolveOperation(residue));
        ret.add(residue);
        ret.add(left);
        return ret;
    }

    @Override
    public void travel()
    {
        if(left instanceof ShiftExpression left2&&left2.right instanceof ShiftExpression)left= rotateLeft(left2);
        left.travel();
        codes.addAll(left.codes);
        if(right!=null)
        {
            resultId=requestInternalVariable(PrimitiveType.BOOL);
            ShiftExpression right2=(ShiftExpression) right;
            if(right2.right instanceof ShiftExpression)right= rotateLeft(right2);
            right.travel();
            codes.addAll(right.codes);
            type=PrimitiveType.BOOL;
            leftId=-1;
            codes.add(new AssignCode(resultId,left.resultId,right.resultId,type,left.type,right.type, op));
        }
        else
        {
            type=left.type;
            leftId=left.leftId;
            isDirect=left.isDirect;
            resultId=left.resultId;
        }
    }
}

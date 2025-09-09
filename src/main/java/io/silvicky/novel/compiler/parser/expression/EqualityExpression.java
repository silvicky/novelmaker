package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.util.Util.getResultType;
import static io.silvicky.novel.util.Util.rotateLeft;

public class EqualityExpression extends LTRExpression
{
    public OperatorType op =null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new RelationalExpression();
        EqualityExpressionResidue residue=new EqualityExpressionResidue(this);
        ret.add(new ResolveOperation(residue));
        ret.add(residue);
        ret.add(left);
        return ret;
    }

    @Override
    public void travel()
    {
        resultId=requestInternalVariable();
        if(left instanceof RelationalExpression left2&&left2.right instanceof RelationalExpression)left= rotateLeft(left2);
        left.travel();
        codes.addAll(left.codes);
        if(right!=null)
        {
            RelationalExpression right2=(RelationalExpression) right;
            if(right2.right instanceof RelationalExpression)right= rotateLeft(right2);
            right.travel();
            codes.addAll(right.codes);
            type=getResultType(left.type,right.type,op);
            leftId=-1;
            codes.add(new AssignCode(resultId,left.resultId,right.resultId,type,left.type,right.type, op));
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

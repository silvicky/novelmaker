package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class ExpressionNew extends LTRExpression
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new AssignmentExpression();
        ExpressionResidue residue=new ExpressionResidue(this);
        ret.add(new ResolveOperation(residue));
        ret.add(residue);
        ret.add(left);
        return ret;
    }

    @Override
    public void travel()
    {
        left.travel();
        codes.addAll(left.codes);
        if(right!=null)
        {
            right.travel();
            codes.addAll(right.codes);
            codes.add(new AssignCode(resultId,left.resultId,right.resultId,OperatorType.COMMA));
        }
        else
        {
            codes.add(new AssignCode(resultId,left.resultId,left.resultId,OperatorType.NOP));
        }
    }
}

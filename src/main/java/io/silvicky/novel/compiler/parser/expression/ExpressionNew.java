package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.util.Pair;

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
            type=right.type;
            leftId=right.leftId;
            isDirect= right.isDirect;
            resultId=right.resultId;
        }
        else
        {
            type=left.type;
            leftId=left.leftId;
            isDirect=left.isDirect;
            resultId=left.resultId;
        }
    }

    @Override
    public Pair<PrimitiveType, Object> evaluateConstExpr()
    {
        //TODO Comma should be forbidden but why?
        if(right!=null)return right.evaluateConstExpr();
        else return left.evaluateConstExpr();
    }
}

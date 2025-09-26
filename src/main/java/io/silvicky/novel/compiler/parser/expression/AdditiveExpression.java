package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.util.Util.*;

public class AdditiveExpression extends LTRExpression
{
    public OperatorType op =null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new MultiplicativeExpression();
        AdditiveExpressionResidue residue=new AdditiveExpressionResidue(this);
        ret.add(new ResolveOperation(residue));
        ret.add(residue);
        ret.add(left);
        return ret;
    }

    @Override
    public void travel()
    {
        if(left instanceof MultiplicativeExpression left2&&left2.right instanceof MultiplicativeExpression)left= rotateLeft(left2);
        left.travel();
        codes.addAll(left.codes);
        if(right!=null)
        {
            MultiplicativeExpression right2=(MultiplicativeExpression) right;
            if(right2.right instanceof MultiplicativeExpression)right= rotateLeft(right2);
            right.travel();
            codes.addAll(right.codes);
            type=getResultType(left.type,right.type,op);
            resultId=requestInternalVariable(type);
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

    @Override
    public Pair<PrimitiveType, Object> evaluateConstExpr()
    {
        if(left instanceof MultiplicativeExpression left2&&left2.right instanceof MultiplicativeExpression)left= rotateLeft(left2);
        if(right!=null)
        {
            MultiplicativeExpression right2=(MultiplicativeExpression) right;
            if(right2.right instanceof MultiplicativeExpression)right= rotateLeft(right2);
            return calculateConstExpr(left.evaluateConstExpr(),right.evaluateConstExpr(),op);
        }
        else return left.evaluateConstExpr();
    }
}

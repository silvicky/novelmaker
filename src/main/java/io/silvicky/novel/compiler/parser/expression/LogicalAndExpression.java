package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.code.raw.AssignNumberCode;
import io.silvicky.novel.compiler.code.raw.AssignUnaryCode;
import io.silvicky.novel.compiler.code.raw.GotoCode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.util.Util.calculateConstExpr;
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
            resultId=requestInternalVariable(PrimitiveType.BOOL);
            leftId=-1;
            type= PrimitiveType.BOOL;
            LabelCode second=new LabelCode();
            LabelCode end=new LabelCode();
            InclusiveOrExpression right2=(InclusiveOrExpression) right;
            if(right2.right instanceof InclusiveOrExpression)right= rotateLeft(right2);
            codes.add(new GotoCode(left.resultId,left.type,false,second.id()));
            codes.add(new AssignNumberCode(resultId,false,type,type));
            codes.add(new UnconditionalGotoCode(end.id()));
            codes.add(second);
            right.travel();
            codes.addAll(right.codes);
            codes.add(new AssignUnaryCode(resultId,right.resultId,type,right.type,OperatorType.NOP));
            codes.add(end);
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
        if(left instanceof InclusiveOrExpression left2&&left2.right instanceof InclusiveOrExpression)left= rotateLeft(left2);
        if(right!=null)
        {
            InclusiveOrExpression right2=(InclusiveOrExpression) right;
            if(right2.right instanceof InclusiveOrExpression)right= rotateLeft(right2);
            return calculateConstExpr(left.evaluateConstExpr(),right.evaluateConstExpr(),OperatorType.AND_AND);
        }
        else return left.evaluateConstExpr();
    }
}

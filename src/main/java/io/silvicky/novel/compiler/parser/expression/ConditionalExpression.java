package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.code.raw.GotoCode;
import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestLabel;
import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.util.Util.getResultType;
import static io.silvicky.novel.util.Util.rotateLeft;

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
        ConditionalExpressionResidue residue=new ConditionalExpressionResidue(this);
        ret.add(new ResolveOperation(residue));
        ret.add(residue);
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
            int lbRight= requestLabel();
            int lbEnd= requestLabel();
            codes.add(new GotoCode(left.resultId,left.type, true,lbRight));
            if(middle.right instanceof ExpressionNew)middle= rotateLeft(middle);
            leftId=-1;
            middle.travel();
            right.travel();
            //TODO any better way?
            type=getResultType(middle.type,right.type,OperatorType.PLUS);
            resultId=requestInternalVariable(type);
            codes.addAll(middle.codes);
            codes.add(new AssignCode(resultId, middle.resultId, middle.resultId, type,middle.type,middle.type,OperatorType.NOP));
            codes.add(new UnconditionalGotoCode(lbEnd));
            codes.add(new LabelCode(lbRight));
            codes.addAll(right.codes);
            codes.add(new AssignCode(resultId, right.resultId,right.resultId,type,right.type,right.type,OperatorType.NOP));
            codes.add(new LabelCode(lbEnd));
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

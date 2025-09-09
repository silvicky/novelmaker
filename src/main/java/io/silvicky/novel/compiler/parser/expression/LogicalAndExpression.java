package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.*;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
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
        //TODO fix this and other logical stuff
        if(left instanceof InclusiveOrExpression left2&&left2.right instanceof InclusiveOrExpression)left= rotateLeft(left2);
        left.travel();
        codes.addAll(left.codes);
        if(right!=null)
        {
            resultId=requestInternalVariable();
            leftId=-1;
            type= PrimitiveType.BOOL;
            LabelCode second=new LabelCode();
            LabelCode end=new LabelCode();
            InclusiveOrExpression right2=(InclusiveOrExpression) right;
            if(right2.right instanceof InclusiveOrExpression)right= rotateLeft(right2);
            codes.add(new GotoCode(left.resultId,0,OperatorType.NOP,second.id()));
            codes.add(new AssignNumberCode(resultId,false,type,type));
            codes.add(new UnconditionalGotoCode(end.id()));
            codes.add(second);
            right.travel();
            codes.addAll(right.codes);
            codes.add(new AssignCode(resultId,right.resultId,0,type,right.type,right.type, OperatorType.NOP));
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
}

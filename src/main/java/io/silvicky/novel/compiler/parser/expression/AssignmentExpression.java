package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.DereferenceCode;
import io.silvicky.novel.compiler.code.IndirectAssignCode;
import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PointerType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;

public class AssignmentExpression extends AbstractExpression implements ASTNode
{
    public ConditionalExpression left=null;
    public OperatorType op=null;
    public AssignmentExpression right=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        left=new ConditionalExpression();
        AssignmentExpressionResidue residue=new AssignmentExpressionResidue(this);
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
            resultId=requestInternalVariable();
            right.travel();
            codes.addAll(right.codes);
            if(left.leftId==-1)throw new GrammarException("not lvalue");
            type= left.type;
            leftId=-1;
            if(left.isDirect)
            {
                codes.add(new AssignCode(left.leftId,left.leftId,right.resultId,type,type,right.type,op.baseType));
                codes.add(new AssignCode(resultId,left.leftId,left.leftId,type,type,type,OperatorType.NOP));
            }
            else
            {
                int realValue=requestInternalVariable();
                codes.add(new DereferenceCode(realValue,left.leftId,new PointerType(type)));
                codes.add(new AssignCode(resultId,realValue,right.resultId,type,type,right.type,op.baseType));
                codes.add(new IndirectAssignCode(left.leftId,resultId,type));
            }
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

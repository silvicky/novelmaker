package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.code.raw.AssignUnaryCode;
import io.silvicky.novel.compiler.code.raw.DereferenceCode;
import io.silvicky.novel.compiler.code.raw.IndirectAssignCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.*;
import io.silvicky.novel.util.Pair;

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
            right.travel();
            codes.addAll(right.codes);
            if(left.leftId==-1)throw new GrammarException("not lvalue");
            type= left.type;
            if(type instanceof ConstType||type instanceof ArrayType||type instanceof FunctionType)throw new GrammarException("modifying const value");
            resultId=requestInternalVariable(type);
            leftId=-1;
            if(left.isDirect)
            {
                if(op==OperatorType.EQUAL)codes.add(new AssignUnaryCode(left.leftId,right.resultId,type,right.type,OperatorType.NOP));
                else codes.add(new AssignCode(left.leftId,left.leftId,right.resultId,type,type,right.type,op.baseType));
                codes.add(new AssignUnaryCode(resultId,left.leftId,type,type,OperatorType.NOP));
            }
            else
            {
                if(op==OperatorType.EQUAL)codes.add(new AssignUnaryCode(resultId,right.resultId,type,right.type,OperatorType.NOP));
                else
                {
                    int realValue = requestInternalVariable(type);
                    codes.add(new DereferenceCode(realValue, left.leftId, new PointerType(type)));
                    codes.add(new AssignCode(resultId, realValue, right.resultId, type, type, right.type, op.baseType));
                }
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

    @Override
    public Pair<PrimitiveType, Object> evaluateConstExpr()
    {
        if(right!=null) throw new GrammarException("assignment forbidden in constexpr");
        else return left.evaluateConstExpr();
    }
}

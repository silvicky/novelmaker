package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariable;

public class AssignmentExpression extends AbstractExpression implements ASTNode
{
    public int left=-1;
    public OperatorType op=null;
    public AssignmentExpression right=null;
    public ConditionalExpression nextExpression=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken&&second instanceof OperatorToken operatorToken&&operatorToken.type.properties== OperatorType.OperatorArgsProperties.BINARY_ASSIGN)
        {
            left=lookupVariable(identifierToken.id);
            op=operatorToken.type;
            right=new AssignmentExpression();
            ret.add(right);
            ret.add(new OperatorToken(op));
            ret.add(new IdentifierToken(identifierToken.id));
        }
        else
        {
            nextExpression=new ConditionalExpression();
            ret.add(nextExpression);
        }
        return ret;
    }

    @Override
    public void travel()
    {
        if(nextExpression==null)
        {
            right.travel();
            codes.addAll(right.codes);
            codes.add(new AssignCode(left,left,right.resultId,op.baseType));
            codes.add(new AssignCode(resultId,left,-1,OperatorType.NOP));
        }
        else
        {
            nextExpression.travel();
            codes.addAll(nextExpression.codes);
            codes.add(new AssignCode(resultId, nextExpression.resultId, -1,OperatorType.NOP));
        }
    }
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariable;

public class AssignmentExpression extends AbstractExpression
{
    public int left;
    public OperatorType op;
    public AssignmentExpression right;
    public ConditionalExpression next;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken&&second instanceof OperatorToken operatorToken&&operatorToken.type.properties== OperatorType.OperatorArgsProperties.BINARY_ASSIGN)
        {
            left=lookupVariable(identifierToken.id);
            op=operatorToken.type;
            right=new AssignmentExpression();
            next=null;
            ret.add(right);
            ret.add(new OperatorToken(op));
            ret.add(new IdentifierToken(identifierToken.id));
        }
        else
        {
            left=-1;
            op=null;
            right=null;
            next=new ConditionalExpression();
            ret.add(next);
        }
        return ret;
    }
}

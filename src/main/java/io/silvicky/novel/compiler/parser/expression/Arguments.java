package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Arguments extends AbstractExpression implements ASTNode
{
    private final Postfix func;
    private AssignmentExpression left=null;
    public Arguments right=null;
    public Arguments(Postfix func)
    {
        this.func = func;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.R_PARENTHESES)return ret;
        left=new AssignmentExpression();
        ArgumentsResidue residue=new ArgumentsResidue(this,func);
        ret.add(new ResolveOperation(residue));
        ret.add(residue);
        ret.add(left);
        return ret;
    }

    @Override
    public void travel()
    {
        if(left==null)return;
        left.travel();
        codes.addAll(left.codes);
        func.arguments.add(new Pair<>(left.type,left.resultId));
        if(right!=null)
        {
            right.travel();
            codes.addAll(right.codes);
        }
    }
}

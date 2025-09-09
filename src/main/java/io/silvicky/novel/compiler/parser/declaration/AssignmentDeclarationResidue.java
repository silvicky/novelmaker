package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.line.Block;
import io.silvicky.novel.compiler.parser.operation.Skip;
import io.silvicky.novel.compiler.parser.expression.AbstractExpressionResidue;
import io.silvicky.novel.compiler.parser.expression.AssignmentExpression;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class AssignmentDeclarationResidue extends AbstractExpressionResidue<AssignmentDeclaration>
{
    private AssignmentExpression assignmentExpression=null;
    private Block functionBody=null;
    protected AssignmentDeclarationResidue(AssignmentDeclaration root)
    {
        super(root);
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken)
        {
            if(operatorToken.type== OperatorType.EQUAL)
            {
                assignmentExpression=new AssignmentExpression();
                ret.add(new ResolveOperation(this));
                ret.add(assignmentExpression);
                ret.add(new OperatorToken(OperatorType.EQUAL));
                return ret;
            }
            if(operatorToken.type==OperatorType.L_BRACE)
            {
                functionBody=new Block();
                ret.add(new Skip());
                ret.add(new ResolveOperation(this));
                ret.add(new OperatorToken(OperatorType.R_BRACE));
                ret.add(functionBody);
                ret.add(new OperatorToken(OperatorType.L_BRACE));
            }
        }
        return ret;
    }

    @Override
    public void resolve()
    {
        root.assignmentExpression=assignmentExpression;
        root.functionBody=functionBody;
    }
}

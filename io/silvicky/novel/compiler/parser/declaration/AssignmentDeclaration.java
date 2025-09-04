package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.ReturnCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.AssignmentExpression;
import io.silvicky.novel.compiler.parser.line.Block;
import io.silvicky.novel.compiler.parser.operation.ResolveOperation;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.FunctionType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.*;

public class AssignmentDeclaration extends NonTerminal implements ASTNode
{
    private final BaseTypeBuilderRoot baseTypeBuilderRoot;
    public AssignmentExpression assignmentExpression=null;
    public Block functionBody=null;
    private UnaryDeclaration unaryDeclaration;
    public final NonTerminal directParent;

    public AssignmentDeclaration(BaseTypeBuilderRoot baseTypeBuilderRoot, NonTerminal directParent)
    {
        this.baseTypeBuilderRoot = baseTypeBuilderRoot;
        this.directParent = directParent;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        unaryDeclaration=new UnaryDeclaration(baseTypeBuilderRoot);
        AssignmentDeclarationResidue assignmentDeclarationResidue =new AssignmentDeclarationResidue(this);
        ret.add(assignmentDeclarationResidue);
        ret.add(unaryDeclaration);
        return ret;
    }
    @Override
    public void travel()
    {
        unaryDeclaration.travel();
        if(unaryDeclaration.type instanceof FunctionType)
        {
            if(directParent==null)
            {
                registerVariable(unaryDeclaration.name, unaryDeclaration.type);
            }
            else
            {
                //TODO how to even process this stuff??
                registerLocalVariable(unaryDeclaration.name, unaryDeclaration.type);
                directParent.revokedVariables.add(unaryDeclaration.name);
            }
            if(assignmentExpression!=null)
            {
                throw new GrammarException("function assigned as variable");
            }
            if(functionBody!=null)
            {
                ctx=registerLabel(unaryDeclaration.name);
                for(Pair<Type,String> pair: unaryDeclaration.parameters)
                {
                    registerLocalVariable(pair.second(), pair.first());
                    functionBody.revokedVariables.add(pair.second());
                }
                int endLabel=requestLabel();
                codes.add(new UnconditionalGotoCode(endLabel));
                codes.add(new LabelCode(ctx));
                functionBody.travel();
                codes.addAll(functionBody.codes);
                codes.add(new ReturnCode(-1));
                codes.add(new LabelCode(endLabel));
                ctx=-1;
            }
            //fixme
            else throw new GrammarException("function not implemented");
        }
        else
        {
            if(assignmentExpression!=null)
            {
                assignmentExpression.travel();
                codes.addAll(assignmentExpression.codes);
            }
            else if(functionBody!=null)
            {
                throw new GrammarException("variable assigned as function");
            }
            int nid;
            if(directParent==null)
            {
                nid=registerVariable(unaryDeclaration.name, unaryDeclaration.type);
            }
            else
            {
                nid=registerLocalVariable(unaryDeclaration.name, unaryDeclaration.type);
                directParent.revokedVariables.add(unaryDeclaration.name);
            }
            if(assignmentExpression!=null)
            {
                codes.add(new AssignCode(nid,assignmentExpression.resultId,assignmentExpression.resultId, unaryDeclaration.type, assignmentExpression.type, assignmentExpression.type, OperatorType.NOP));
            }
        }
    }
}

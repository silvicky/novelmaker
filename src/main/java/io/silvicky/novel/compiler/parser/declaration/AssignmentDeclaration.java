package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.ReturnCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.code.raw.AssignNumberCode;
import io.silvicky.novel.compiler.code.raw.AssignUnaryCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.AssignmentExpression;
import io.silvicky.novel.compiler.parser.line.Block;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.ConstType;
import io.silvicky.novel.compiler.types.FunctionType;
import io.silvicky.novel.compiler.types.PrimitiveType;
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
        unaryDeclaration=new UnaryDeclaration();
        AssignmentDeclarationResidue assignmentDeclarationResidue =new AssignmentDeclarationResidue(this);
        ret.add(assignmentDeclarationResidue);
        ret.add(unaryDeclaration);
        return ret;
    }
    @Override
    public void travel()
    {
        //TODO Register into root's field list
        unaryDeclaration.receivedType= baseTypeBuilderRoot.type;
        unaryDeclaration.travel();
        if(unaryDeclaration.name==null)throw new GrammarException("bare anonymous variable");
        if(unaryDeclaration.type instanceof FunctionType functionType)
        {
            int nid;
            if(directParent==null)
            {
                Pair<Integer,Type> pr=lookupVariable(unaryDeclaration.name);
                if(pr==null)nid = registerVariable(unaryDeclaration.name, unaryDeclaration.type);
                else
                {
                    if (pr.second().equals(unaryDeclaration.type))
                    {
                        nid = pr.first();
                    }
                    else throw new RuntimeException("conflict function declaration");
                }
            }
            else if(directParent instanceof DeclarationBlock)
            {
                //TODO This is OOP methods, common in C++ and Java but not C
                throw new RuntimeException("class method unsupported");
            }
            else
            {
                //TODO This is nested/lambda function, which is (in principle) forbidden in C but we should make it
                nid=registerLocalVariable(unaryDeclaration.name, unaryDeclaration.type);
                directParent.revokedVariables.add(unaryDeclaration.name);
            }
            if(assignmentExpression!=null)
            {
                throw new GrammarException("function assigned as variable");
            }
            if(functionBody!=null)
            {
                ctx= registerLabel(unaryDeclaration.name);
                returnType=functionType.returnType();
                codes.add(new AssignNumberCode(nid,ctx, PrimitiveType.INT,PrimitiveType.INT));
                for(Pair<Type,String> pair: unaryDeclaration.parameters)
                {
                    if(pair.first()==PrimitiveType.ELLIPSIS)continue;
                    registerParameter(pair.second(), pair.first());
                    functionBody.revokedVariables.add(pair.second());
                }
                int endLabel= registerLabel("0"+unaryDeclaration.name);
                codes.add(new UnconditionalGotoCode(endLabel));
                codes.add(new LabelCode(ctx));
                functionBody.travel();
                codes.addAll(functionBody.codes);
                codes.add(new ReturnCode(-1));
                codes.add(new LabelCode(endLabel));
                ctx=-1;
                returnType=null;
            }
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
            if(unaryDeclaration.type.isAuto())
            {
                //TODO this is very wrong, for example, C++ standard forces all declarations in one line to resolve into a same type, and pointers are thus not meaningless
                if(assignmentExpression==null)throw new GrammarException("auto variable not initialized");
                Type type=assignmentExpression.type;
                if(unaryDeclaration.type instanceof ConstType)
                {
                    type=new ConstType(type);
                }
                unaryDeclaration.type=type;
            }
            int nid;
            if(directParent==null)
            {
                nid=registerVariable(unaryDeclaration.name, unaryDeclaration.type);
            }
            else if(directParent instanceof DeclarationBlock declarationBlock)
            {
                declarationBlock.fields.add(new Pair<>(unaryDeclaration.name,unaryDeclaration.type));
                return;
            }
            else
            {
                nid=registerLocalVariable(unaryDeclaration.name, unaryDeclaration.type);
                directParent.revokedVariables.add(unaryDeclaration.name);
            }
            if(assignmentExpression!=null)
            {
                codes.add(new AssignUnaryCode(nid,assignmentExpression.resultId, unaryDeclaration.type, assignmentExpression.type, OperatorType.NOP));
            }
        }
    }
}

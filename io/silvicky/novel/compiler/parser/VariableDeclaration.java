package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.parser.expression.AssignmentExpression;
import io.silvicky.novel.compiler.parser.operation.AppendCodeOperation;
import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.registerLocalVariable;
import static io.silvicky.novel.compiler.Compiler.registerVariable;

public class VariableDeclaration extends NonTerminal
{
    public final NonTerminal directParent;

    public VariableDeclaration(NonTerminal directParent)
    {
        this.directParent = directParent;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(!(next instanceof IdentifierToken identifierToken))
        {
            throw new GrammarException(this.getClass().getSimpleName()+next);
        }
        if(!(second instanceof OperatorToken operatorToken))
        {
            throw new GrammarException(this.getClass().getSimpleName()+next+second);
        }
        if(operatorToken.type == OperatorType.COMMA||operatorToken.type==OperatorType.SEMICOLON)
        {
            String id=identifierToken.id;
            if(this.directParent==null)registerVariable(id);
            else
            {
                registerLocalVariable(id);
                this.directParent.revokedVariables.add(id);
            }
            VariableDeclarationResidue residue=new VariableDeclarationResidue(this.directParent);
            ret.add(new AppendCodeSeqOperation(this,residue));
            ret.add(residue);
            ret.add(new IdentifierToken(id));
            return ret;
        }
        else if(operatorToken.type==OperatorType.EQUAL)
        {
            String id=identifierToken.id;
            int nid;
            if(this.directParent==null)nid=registerVariable(id);
            else
            {
                nid=registerLocalVariable(id);
                this.directParent.revokedVariables.add(id);
            }
            VariableDeclarationResidue residue=new VariableDeclarationResidue(this.directParent);
            ret.add(new AppendCodeSeqOperation(this,residue));
            ret.add(residue);
            AssignmentExpression expression=new AssignmentExpression();
            ret.add(new AppendCodeOperation(this,new AssignCode(nid,expression.resultId,0,OperatorType.NOP)));
            ret.add(new AppendCodeSeqOperation(this,expression));
            ret.add(expression);
            ret.add(new OperatorToken(OperatorType.EQUAL));
            ret.add(new IdentifierToken(id));
            return ret;
        }
        throw new GrammarException(this.getClass().getSimpleName()+next+second);
    }
}

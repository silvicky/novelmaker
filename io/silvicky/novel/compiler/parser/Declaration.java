package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

public class Declaration extends NonTerminal
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(!(next instanceof IdentifierToken))
        {
            throw new GrammarException(this.getClass().getSimpleName()+next);
        }
        if(second instanceof OperatorToken&&(((OperatorToken) second).type== OperatorType.COMMA||((OperatorToken) second).type==OperatorType.SEMICOLON||((OperatorToken) second).type==OperatorType.EQUAL))
        {
            VariableDeclaration variableDeclaration=new VariableDeclaration(null);
            ret.add(new OperatorToken(OperatorType.SEMICOLON));
            ret.add(new AppendCodeSeqOperation(this,variableDeclaration));
            ret.add(variableDeclaration);
            return ret;
        }
        if(second instanceof OperatorToken&&((OperatorToken) second).type==OperatorType.L_PARENTHESES)
        {
            FunctionDeclaration functionDeclaration=new FunctionDeclaration();
            ret.add(new AppendCodeSeqOperation(this,functionDeclaration));
            ret.add(functionDeclaration);
            return ret;
        }
        throw new GrammarException(this.getClass().getSimpleName()+next+second);
    }
}

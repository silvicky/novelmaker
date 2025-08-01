package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.registerLocalVariable;

public class Arguments extends NonTerminal
{
    private final int functionId;

    public Arguments(int functionId)
    {
        this.functionId = functionId;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.R_PARENTHESES)return ret;
        if(!(second instanceof IdentifierToken identifierToken))throw new GrammarException("arg not identifier");
        registerLocalVariable(identifierToken.id);
        ret.add(new ArgumentsResidue(functionId));
        ret.add(new IdentifierToken(identifierToken.id));
        ret.add(new KeywordToken(KeywordType.INT));
        return ret;
    }
}

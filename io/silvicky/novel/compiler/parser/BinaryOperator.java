package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class BinaryOperator extends NonTerminal
{
    public OperatorType operatorType;
    @Override
    public List<Token> lookup(Token next, Token second)
    {
        List<Token> ret=new ArrayList<>();
        if(next instanceof OperatorToken&&((OperatorToken) next).type().properties== OperatorType.OperatorArgsProperties.BINARY)
        {
            operatorType=((OperatorToken) next).type();
            ret.add(new OperatorToken(operatorType));
            return ret;
        }
        throw new GrammarException(this.getClass().getSimpleName()+next);
    }
}

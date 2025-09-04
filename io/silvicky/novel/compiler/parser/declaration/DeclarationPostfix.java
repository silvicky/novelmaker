package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.NumberToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class DeclarationPostfix extends NonTerminal
{
    public OperatorType operatorType;
    public int size=-1;
    public final List<Pair<Type,String>> parameters=new ArrayList<>();

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {

        List<AbstractToken> ret=new ArrayList<>();
        operatorType=((OperatorToken) next).type;
        if(operatorType==OperatorType.L_BRACKET)
        {
            size=((NumberToken<Integer>)second).value;
            //TODO Allow constant expression
            ret.add(new OperatorToken(OperatorType.R_BRACKET));
            ret.add(new NumberToken<>(size, PrimitiveType.INT));
            ret.add(new OperatorToken(OperatorType.L_BRACKET));
        }
        else
        {
            ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
            ret.add(new Arguments(this));
            ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        }
        return ret;
    }
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class Postfix extends NonTerminal implements ASTNode
{
    public OperatorType operatorType;
    public ExpressionNew nextExpression;
    public final List<Integer> parameters=new ArrayList<>();
    private Parameters pr;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        operatorType=((OperatorToken) next).type;
        if(operatorType==OperatorType.PLUS_PLUS||operatorType==OperatorType.MINUS_MINUS)
        {
            ret.add(new OperatorToken(operatorType));
        }
        else if(operatorType==OperatorType.L_BRACKET)
        {
            ret.add(new OperatorToken(OperatorType.R_BRACKET));
            nextExpression=new ExpressionNew();
            ret.add(nextExpression);
            ret.add(new OperatorToken(OperatorType.L_BRACKET));
        }
        else
        {
            ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
            pr=new Parameters(this);
            ret.add(pr);
            ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        }
        return ret;
    }

    @Override
    public void travel()
    {
        //TODO
    }
}

package io.silvicky.novel.compiler.parser.expression;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Postfix extends NonTerminal implements ASTNode
{
    public OperatorType operatorType;
    public ExpressionNew nextExpression;
    public final List<Pair<Type,Integer>> arguments =new ArrayList<>();
    private Arguments pr;
    public String memberName;
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
        else if(operatorType==OperatorType.L_PARENTHESES)
        {
            ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
            pr=new Arguments(this);
            ret.add(pr);
            ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        }
        else if(operatorType==OperatorType.DOT)
        {
            memberName=((IdentifierToken)second).id;
            ret.add(new IdentifierToken(memberName));
            ret.add(new OperatorToken(OperatorType.DOT));
        }
        else
        {
            memberName=((IdentifierToken)second).id;
            ret.add(new IdentifierToken(memberName));
            ret.add(new OperatorToken(OperatorType.INDIRECT_ACCESS));
        }
        return ret;
    }

    @Override
    public void travel()
    {
        if(nextExpression!=null)
        {
            nextExpression.travel();
            codes.addAll(nextExpression.codes);
        }
        if(pr!=null)
        {
            pr.travel();
            codes.addAll(pr.codes);
        }
    }
}

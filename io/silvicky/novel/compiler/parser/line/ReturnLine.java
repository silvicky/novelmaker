package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.ReturnCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class ReturnLine extends NonTerminal implements ASTNode
{
    private ExpressionRoot expressionRoot;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        expressionRoot=new ExpressionRoot();
        ret.add(new OperatorToken(OperatorType.SEMICOLON));
        ret.add(expressionRoot);
        ret.add(new KeywordToken(KeywordType.RETURN));
        return ret;
    }

    @Override
    public void travel()
    {
        expressionRoot.travel();
        codes.addAll(expressionRoot.codes);
        //TODO Cast
        codes.add(new ReturnCode(expressionRoot.resultId));
    }
}

package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.code.ReturnCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;
import static io.silvicky.novel.compiler.Compiler.returnType;

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
        if(!returnType.equals(expressionRoot.type))
        {
            int t1 = requestInternalVariable();
            codes.add(new AssignCode(t1, expressionRoot.resultId, expressionRoot.resultId, returnType, expressionRoot.type, expressionRoot.type, OperatorType.NOP));
            codes.add(new ReturnCode(t1));
        }
        else
        {
            codes.add(new ReturnCode(expressionRoot.resultId));
        }
    }
}

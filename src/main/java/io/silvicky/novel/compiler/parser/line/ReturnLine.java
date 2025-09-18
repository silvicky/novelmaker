package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.raw.AssignCode;
import io.silvicky.novel.compiler.code.ReturnCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.PrimitiveType;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.*;

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
        if(returnType==PrimitiveType.VOID)
        {
            if(!expressionRoot.codes.isEmpty())throw new GrammarException("void function should not return expression");
            codes.add(new ReturnCode(-1,argSize));
            return;
        }
        codes.addAll(expressionRoot.codes);
        if(!returnType.equals(expressionRoot.type))
        {
            int t1 = requestInternalVariable(returnType);
            codes.add(new AssignCode(t1, expressionRoot.resultId, expressionRoot.resultId, returnType, expressionRoot.type, expressionRoot.type, OperatorType.NOP));
            codes.add(new ReturnCode(t1,argSize));
        }
        else
        {
            codes.add(new ReturnCode(expressionRoot.resultId,argSize));
        }
    }
}

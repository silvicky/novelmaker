package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.code.PlaceholderUnconditionalGotoCode;
import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class GotoLine extends NonTerminal implements ASTNode
{
    private String id;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(!(second instanceof IdentifierToken identifierToken))
        {
            throw new GrammarException("goto label not named with an identifier");
        }
        id=identifierToken.id;
        ret.add(new OperatorToken(OperatorType.SEMICOLON));
        ret.add(new IdentifierToken(identifierToken.id));
        ret.add(new KeywordToken(KeywordType.GOTO));
        return ret;
    }

    @Override
    public void travel()
    {
        codes.add(new PlaceholderUnconditionalGotoCode(id));
    }
}

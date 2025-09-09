package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.KeywordToken;
import io.silvicky.novel.compiler.tokens.KeywordType;

import java.util.ArrayList;
import java.util.List;

public class Else extends NonTerminal implements ASTNode
{
    public final int breakLabel,continueLabel;
    private Line line=null;
    public Else(int breakLabel,int continueLabel){this.breakLabel=breakLabel;this.continueLabel=continueLabel;}
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof KeywordToken keywordToken&&keywordToken.type== KeywordType.ELSE)
        {
            line=new Line(breakLabel,continueLabel,null);
            ret.add(line);
            ret.add(new KeywordToken(KeywordType.ELSE));
        }
        return ret;
    }

    @Override
    public void travel()
    {
        if(line!=null)
        {
            line.travel();
            codes.addAll(line.codes);
        }
    }
}

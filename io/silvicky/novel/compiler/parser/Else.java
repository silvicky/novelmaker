package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Else extends NonTerminal
{
    public final int breakLabel,continueLabel;
    public Else(int breakLabel,int continueLabel){this.breakLabel=breakLabel;this.continueLabel=continueLabel;}
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof KeywordToken keywordToken&&keywordToken.type== KeywordType.ELSE)
        {
            Line line=new Line(breakLabel,continueLabel,this);
            ret.add(new AppendCodeSeqOperation(this,line));
            ret.add(line);
            ret.add(new KeywordToken(KeywordType.ELSE));
        }
        return ret;
    }
}

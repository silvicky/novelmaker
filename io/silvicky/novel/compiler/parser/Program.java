package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Program extends NonTerminal
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next==null)return ret;
        if(next instanceof KeywordToken keywordToken&&keywordToken.type== KeywordType.INT)
        {
            ret.add(new Program());
            ret.add(new Declaration());
            ret.add(new KeywordToken(KeywordType.INT));
            return ret;
        }
        throw new GrammarException(this.getClass().getSimpleName()+next);
    }
}

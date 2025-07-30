package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Program extends NonTerminal
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next==null||next instanceof EofToken)return ret;
        if(next instanceof KeywordToken keywordToken&&keywordToken.type== KeywordType.INT)
        {
            Declaration declaration=new Declaration();
            Program program=new Program();
            ret.add(new AppendCodeSeqOperation(this,program));
            ret.add(program);
            ret.add(new AppendCodeSeqOperation(this,declaration));
            ret.add(declaration);
            ret.add(new KeywordToken(KeywordType.INT));
            return ret;
        }
        throw new GrammarException(this.getClass().getSimpleName()+next);
    }
}

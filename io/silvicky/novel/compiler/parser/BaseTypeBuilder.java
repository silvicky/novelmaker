package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.KeywordToken;
import io.silvicky.novel.compiler.tokens.KeywordType;

import java.util.ArrayList;
import java.util.List;

public class BaseTypeBuilder extends NonTerminal
{
    private final BaseTypeBuilderRoot root;

    public BaseTypeBuilder(BaseTypeBuilderRoot root)
    {
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof KeywordToken keywordToken&&(
                keywordToken.type== KeywordType.UNSIGNED
                ||keywordToken.type==KeywordType.INT
                ||keywordToken.type==KeywordType.FLOAT
                ||keywordToken.type==KeywordType.BOOL
                ||keywordToken.type==KeywordType.DOUBLE
                ||keywordToken.type==KeywordType.VOID
                ||keywordToken.type==KeywordType.CONST
                ||keywordToken.type==KeywordType.SHORT
                ||keywordToken.type==KeywordType.LONG
                ||keywordToken.type==KeywordType.CHAR
                ||keywordToken.type==KeywordType.SIGNED))
        {
            root.keywordTypeList.add(keywordToken.type);
            ret.add(new BaseTypeBuilder(root));
            ret.add(new KeywordToken(keywordToken.type));
        }
        return ret;
    }
}

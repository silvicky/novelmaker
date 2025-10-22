package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.KeywordType;
import io.silvicky.novel.compiler.types.*;

import java.util.ArrayList;
import java.util.List;

public class BaseTypeBuilderRoot extends NonTerminal implements ASTNode
{
    public List<KeywordType> keywordTypeList=new ArrayList<>();
    public Type type;
    public StructDeclaration structDeclaration=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        ret.add(new BaseTypeBuilder(this));
        return ret;
    }

    @Override
    public void travel()
    {
        PrimitiveType primitiveType=null;
        boolean isConst=false;
        int longCount=0;
        boolean isSigned=true;
        boolean isShort=false;
        for(KeywordType keywordType:keywordTypeList)
        {
            if(keywordType==KeywordType.CONST)
            {
                isConst=true;
                continue;
            }
            if(keywordType==KeywordType.SIGNED)
            {
                isSigned=true;
                continue;
            }
            if(keywordType==KeywordType.UNSIGNED)
            {
                isSigned=false;
                continue;
            }
            if(keywordType==KeywordType.LONG)
            {
                longCount++;
                continue;
            }
            if(keywordType==KeywordType.SHORT)
            {
                isShort=true;
                continue;
            }
            if(primitiveType!=null)throw new GrammarException("Repeated type");
            primitiveType=PrimitiveType.valueOf(keywordType.name());
        }
        if(primitiveType==null)primitiveType=PrimitiveType.INT;
        if(longCount>=3)
        {
            throw new GrammarException("Too many longs");
        }
        else if(longCount==2)
        {
            if(primitiveType==PrimitiveType.INT)primitiveType=PrimitiveType.LONG_LONG;
            else throw new GrammarException("Unknown type: long long "+primitiveType.symbol);
        }
        else if(longCount==1)
        {
            if(primitiveType==PrimitiveType.INT)primitiveType=PrimitiveType.LONG;
            else if(primitiveType==PrimitiveType.DOUBLE)primitiveType=PrimitiveType.LONG_DOUBLE;
            else throw new GrammarException("Unknown type: long "+primitiveType.symbol);
        }
        if(isShort)
        {
            if(primitiveType==PrimitiveType.INT)primitiveType=PrimitiveType.SHORT;
            else throw new GrammarException("Unknown type: short "+primitiveType.symbol);
        }
        if(!isSigned)
        {
            if(primitiveType==PrimitiveType.INT)primitiveType=PrimitiveType.UNSIGNED_INT;
            else if(primitiveType==PrimitiveType.CHAR)primitiveType=PrimitiveType.UNSIGNED_CHAR;
            else if(primitiveType==PrimitiveType.LONG)primitiveType=PrimitiveType.UNSIGNED_LONG;
            else if(primitiveType==PrimitiveType.LONG_LONG)primitiveType=PrimitiveType.UNSIGNED_LONG_LONG;
            else if(primitiveType==PrimitiveType.SHORT)primitiveType=PrimitiveType.UNSIGNED_SHORT;
            else throw new GrammarException("Unknown type: unsigned "+primitiveType.symbol);
        }
        if(isConst)type=new ConstType(primitiveType);
        else type=primitiveType;
    }
}

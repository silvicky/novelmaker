package io.silvicky.novel.compiler.tokens;

import io.silvicky.novel.compiler.types.PrimitiveType;

public class CharTokenBuilder extends TokenBuilder
{
    private char val;
    private boolean isEnclosed=false;
    private final CharBuilder charBuilder=new CharBuilder();
    public CharTokenBuilder(String fileName, int line, int pos)
    {
        super(fileName, line, pos);
    }
    @Override
    public boolean append(char c)
    {
        if(isEnclosed)return false;
        if(c=='\'')
        {
            if(charBuilder.isEscape&&charBuilder.append(c))return true;
            val=charBuilder.build();
            isEnclosed=true;
            return true;
        }
        if(charBuilder.append(c))return true;
        throw new InvalidTokenException("invalid char");
    }
    @Override
    public AbstractToken build()
    {
        if(!isEnclosed)throw new InvalidTokenException("invalid char");
        if(val>=256)throw new InvalidTokenException("invalid C char");
        return new NumberToken<>((byte)val, PrimitiveType.CHAR,fileName,line,pos);
    }
}

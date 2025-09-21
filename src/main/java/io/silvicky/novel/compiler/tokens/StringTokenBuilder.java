package io.silvicky.novel.compiler.tokens;

public class StringTokenBuilder extends TokenBuilder
{
    public StringTokenBuilder(String fileName, int line, int pos)
    {
        super(fileName, line, pos);
    }
    private final StringBuilder stringBuilder=new StringBuilder();
    private CharBuilder charBuilder=new CharBuilder();
    private boolean isEnclosed=false;
    @Override
    public boolean append(char c)
    {
        if(isEnclosed)return false;
        if(c=='\"')
        {
            if(charBuilder.isEscape&&charBuilder.append(c))return true;
            if(charBuilder.isReady)stringBuilder.append(charBuilder.build());
            isEnclosed=true;
            return true;
        }
        if(charBuilder.append(c))return true;
        stringBuilder.append(charBuilder.build());
        charBuilder=new CharBuilder();
        charBuilder.append(c);
        return true;
    }

    @Override
    public AbstractToken build()
    {
        if(!isEnclosed)throw new InvalidTokenException("invalid string");
        return new StringToken(stringBuilder.toString(),fileName,line,pos);
    }
}

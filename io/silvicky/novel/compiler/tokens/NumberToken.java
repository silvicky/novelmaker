package io.silvicky.novel.compiler.tokens;

public class NumberToken extends Token
{
    public final long value;
    public NumberToken(long value){super();this.value=value;}
    public NumberToken(long value, String fileName, int line, int pos)
    {
        super(fileName,line,pos);
        this.value = value;
    }
    @Override
    public String toString()
    {
        if(line==-1)
        {
            return String.format("'%d'",value);
        }
        return String.format("'%d'@(%s,%d,%d)",value,fileName,line,pos);
    }
}

package io.silvicky.novel.compiler.tokens;

public class StringToken extends Token
{
    public final String content;

    public StringToken(String content, String fileName, int line, int pos)
    {
        super(fileName,line,pos);
        this.content = content;
    }
    @Override
    public String toString()
    {
        if(line==-1)
        {
            return String.format("\"%s\"",content);
        }
        return String.format("\"%s\"@(%s,%d,%d)",content,fileName,line,pos);
    }
}

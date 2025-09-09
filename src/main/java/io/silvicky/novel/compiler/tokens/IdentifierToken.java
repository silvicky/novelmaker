package io.silvicky.novel.compiler.tokens;

public class IdentifierToken extends Token
{
    public final String id;
    public IdentifierToken(String id){super();this.id=id;}
    public IdentifierToken(String id, String fileName, int line, int pos)
    {
        super(fileName,line,pos);
        this.id = id;
    }
    @Override
    public String toString()
    {
        if(line==-1)
        {
            return String.format("'%s'",id);
        }
        return String.format("'%s'@(%s,%d,%d)",id,fileName,line,pos);
    }
}

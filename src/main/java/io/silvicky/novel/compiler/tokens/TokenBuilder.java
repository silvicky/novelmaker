package io.silvicky.novel.compiler.tokens;

public class TokenBuilder
{
    private TokenBuilder tokenBuilder=null;
    public final String fileName;
    public final int line;
    public final int pos;

    public TokenBuilder(String fileName, int line, int pos)
    {
        this.fileName = fileName;
        this.line = line;
        this.pos = pos;
    }

    public boolean append(char c)
    {
        if(tokenBuilder!=null)return tokenBuilder.append(c);
        if(Character.isDigit(c))
        {
            tokenBuilder=new NumberTokenBuilder(fileName,line,pos);
            tokenBuilder.append(c);
            return true;
        }
        else if(Character.isLetter(c)||c=='_')
        {
            tokenBuilder=new IdentifierTokenBuilder(fileName,line,pos);
            tokenBuilder.append(c);
            return true;
        }
        else if(c=='\'')
        {
            tokenBuilder=new CharTokenBuilder(fileName,line,pos);
            return true;
        }
        else if(c=='\"')
        {
            tokenBuilder=new StringTokenBuilder(fileName,line,pos);
            return true;
        }
        else if(OperatorType.find(String.valueOf(c))!=null)
        {
            tokenBuilder=new OperatorTokenBuilder(fileName,line,pos);
            tokenBuilder.append(c);
            return true;
        }
        else
        {
            return false;
        }
    }
    public AbstractToken build()
    {
        if(tokenBuilder==null)return null;
        return tokenBuilder.build();
    }
}

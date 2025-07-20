package io.silvicky.novel.compiler.tokens;

public class TokenBuilder
{
    TokenBuilder tokenBuilder=null;
    public boolean append(char c)
    {
        if(tokenBuilder!=null)return tokenBuilder.append(c);
        if(Character.isDigit(c))
        {
            //TODO: sign
            tokenBuilder=new NumberTokenBuilder();
            tokenBuilder.append(c);
            return true;
        }
        else if(Character.isLetter(c)||c=='_')
        {
            tokenBuilder=new IdentifierTokenBuilder();
            tokenBuilder.append(c);
            return true;
        }
        else if(OperatorType.find(String.valueOf(c))!=null)
        {
            tokenBuilder=new OperatorTokenBuilder();
            tokenBuilder.append(c);
            return true;
        }
        else
        {
            return false;
        }
    }
    public Token build()
    {
        if(tokenBuilder==null)return null;
        return tokenBuilder.build();
    }
}

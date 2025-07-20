package io.silvicky.novel.compiler.tokens;

public class IdentifierTokenBuilder extends TokenBuilder
{
    private final StringBuilder stringBuilder=new StringBuilder();
    @Override
    public boolean append(char c)
    {
        if(Character.isLetter(c)||Character.isDigit(c)||c=='_')
        {
            stringBuilder.append(c);
            return true;
        }
        return false;
    }
    @Override
    public Token build()
    {
        KeywordType type=KeywordType.find(stringBuilder.toString());
        if(type!=null)return new KeywordToken(type);
        return new IdentifierToken(stringBuilder.toString());
    }
}

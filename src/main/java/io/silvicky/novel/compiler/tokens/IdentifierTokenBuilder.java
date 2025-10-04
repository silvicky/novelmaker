package io.silvicky.novel.compiler.tokens;

public class IdentifierTokenBuilder extends TokenBuilder
{
    private final StringBuilder stringBuilder=new StringBuilder();

    public IdentifierTokenBuilder(String fileName, int line, int pos)
    {
        super(fileName, line, pos);
    }

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
    public AbstractToken build()
    {
        return new IdentifierToken(stringBuilder.toString(),fileName,line,pos);
    }
}

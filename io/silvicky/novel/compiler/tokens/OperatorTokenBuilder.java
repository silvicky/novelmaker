package io.silvicky.novel.compiler.tokens;

public class OperatorTokenBuilder extends TokenBuilder
{
    private final StringBuilder stringBuilder=new StringBuilder();
    @Override
    public boolean append(char c)
    {
        if(OperatorType.find(stringBuilder.toString()+c)==null)return false;
        if(OperatorType.find(String.valueOf(c))==null)return false;
        stringBuilder.append(c);
        return true;
    }
    @Override
    public Token build()
    {
        OperatorType type=OperatorType.find(stringBuilder.toString());
        return new OperatorToken(type);
    }
}

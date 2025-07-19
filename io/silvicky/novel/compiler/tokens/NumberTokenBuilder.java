package io.silvicky.novel.compiler.tokens;

public class NumberTokenBuilder extends TokenBuilder
{
    private enum Sign
    {
        UNKNOWN(1),
        POSITIVE(1),
        NEGATIVE(-1);
        public int value;
        Sign(int value){this.value=value;}
    }
    long abs=0;
    Sign sign=Sign.UNKNOWN;
    @Override
    public boolean append(char c) throws InvalidTokenException
    {
        if(sign==Sign.UNKNOWN)
        {
            if(c=='-')
            {
                sign=Sign.NEGATIVE;
                return true;
            }
            else if(c=='+')
            {
                sign=Sign.POSITIVE;
                return true;
            }
            sign=Sign.POSITIVE;
        }
        if(Character.isDigit(c))
        {
            abs*=10;
            abs+=c-'0';
            return true;
        }
        if(Character.isLetter(c))
        {
            throw new InvalidTokenException(String.valueOf(abs * sign.value)+c+"...");
        }
        return false;
    }
    @Override
    public Token build(){return new NumberToken(abs*sign.value);}
}

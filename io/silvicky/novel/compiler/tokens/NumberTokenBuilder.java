package io.silvicky.novel.compiler.tokens;

public class NumberTokenBuilder extends TokenBuilder
{
    public NumberTokenBuilder(String fileName, int line, int pos)
    {
        super(fileName, line, pos);
    }

    private enum Sign
    {
        UNKNOWN(1),
        POSITIVE(1),
        NEGATIVE(-1);
        public final int value;
        Sign(int value){this.value=value;}
    }
    long abs=0;
    Sign sign=Sign.UNKNOWN;
    @Override
    public boolean append(char c)
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
    public AbstractToken build(){return new NumberToken(abs*sign.value,fileName,line,pos);}
}

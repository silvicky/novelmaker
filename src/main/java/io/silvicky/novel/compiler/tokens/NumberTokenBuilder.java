package io.silvicky.novel.compiler.tokens;

import static io.silvicky.novel.compiler.types.PrimitiveType.*;

public class NumberTokenBuilder extends TokenBuilder
{
    public NumberTokenBuilder(String fileName, int line, int pos)
    {
        super(fileName, line, pos);
    }
    long abs=0;
    double tail=0;
    double tailLevel;
    boolean isDotted=false;
    boolean isExponent=false;
    int base=0;
    int exponent=0;
    boolean isExponentNegative=false;
    boolean isExponentPositive=false;
    StringBuilder suffix=new StringBuilder();
    @Override
    public boolean append(char c)
    {
        //TODO begin with dot
        if(c=='\'')return true;
        if(c=='-')
        {
            if((!isExponent)||isExponentNegative||isExponentPositive)return false;
            isExponentNegative=true;
            return true;
        }
        if(c=='+')
        {
            if((!isExponent)||isExponentNegative||isExponentPositive)return false;
            isExponentPositive=true;
            return true;
        }
        if(Character.isLetter(c))
        {
            if(base==8&&abs==0)
            {
                if(c=='x'||c=='X')
                {
                    base = 16;
                    return true;
                }
                if(c=='b'||c=='B')
                {
                    base=2;
                    return true;
                }
            }
            if(base==16)
            {
                c=Character.toLowerCase(c);
                if(c=='p')
                {
                    if(isExponent)throw new InvalidTokenException("p after p");
                    isExponent=true;
                    return true;
                }
                if(c>'f')throw new InvalidTokenException("invalid number");
                if(!isDotted)
                {
                    abs*=base;
                    abs+=c-'a'+10;
                }
                else
                {
                    tail+=tailLevel*(c-'a'+10);
                    tailLevel/=base;
                }
                return true;
            }
            if(base==10)
            {
                if(c=='e'||c=='E')
                {
                    if(isExponent)throw new InvalidTokenException("e after e");
                    isExponent=true;
                    return true;
                }
            }
            suffix.append(c);
            return true;
        }
        if(!suffix.isEmpty()){throw new InvalidTokenException(String.valueOf(abs+tail)+suffix+c+"...");}
        if(Character.isDigit(c))
        {
            if(isExponent)
            {
                exponent*=10;
                exponent+=c-'0';
                return true;
            }
            if(base==0)
            {
                if(c=='0') base=8;
                else base=10;
            }
            if(c-'0'>=base)throw new InvalidTokenException("invalid number");
            if(!isDotted)
            {
                abs*=base;
                abs+=c-'0';
            }
            else
            {
                tail+=tailLevel*(c-'0');
                tailLevel/=base;
            }
            return true;
        }
        else if(c=='.')
        {
            if(isDotted)throw new InvalidTokenException("dot after dot");
            if(base==2||base==8)throw new InvalidTokenException("bin/oct float");
            if(base==0)base=10;
            isDotted=true;
            tailLevel=1.0/base;
            return true;
        }
        return false;
    }
    @Override
    public AbstractToken build()
    {
        if(isExponent)
        {
            double val=abs+tail;
            if(isExponentNegative)
            {
                for(int i=0;i<exponent;i++)val/=base;
            }
            else
            {
                for(int i=0;i<exponent;i++)val*=base;
            }
            return switch (suffix.toString().toLowerCase())
            {
                case "" -> new NumberToken<>(val, DOUBLE, fileName, line, pos);
                case "l" -> new NumberToken<>(val, LONG_DOUBLE, fileName, line, pos);
                case "f" -> new NumberToken<>((float) (val), FLOAT, fileName, line, pos);
                default -> throw new InvalidTokenException("Unknown suffix: " + suffix.toString().toLowerCase());
            };
        }
        switch (suffix.toString().toLowerCase())
        {
            case "":
            {
                if(isDotted)return new NumberToken<>(abs + tail, DOUBLE,fileName, line, pos);
                else return new NumberToken<>((int)abs,INT,fileName,line,pos);
            }
            case "u":
            {
                if(isDotted)throw new InvalidTokenException("\"u\" cannot be applied to double");
                else return new NumberToken<>((int)abs,UNSIGNED_INT,fileName,line,pos);
            }
            case "l":
            {
                if(isDotted)return new NumberToken<>(abs+tail,LONG_DOUBLE,fileName,line,pos);
                else return new NumberToken<>(abs,LONG,fileName,line,pos);
            }
            case "ul":
            {
                if(isDotted)throw new InvalidTokenException("\"ul\" cannot be applied to double");
                else return new NumberToken<>(abs,UNSIGNED_LONG,fileName,line,pos);
            }
            case "ll":
            {
                if(isDotted)throw new InvalidTokenException("\"ll\" cannot be applied to double");
                else return new NumberToken<>(abs,LONG_LONG,fileName,line,pos);
            }
            case "ull":
            {
                if(isDotted)throw new InvalidTokenException("\"ull\" cannot be applied to double");
                else return new NumberToken<>(abs,UNSIGNED_LONG_LONG,fileName,line,pos);
            }
            case "f":
            {
                return new NumberToken<>((float)(abs+tail),FLOAT,fileName,line,pos);
            }
            default:
            {
                throw new InvalidTokenException("Unknown suffix: "+suffix.toString().toLowerCase());
            }
        }
    }
}

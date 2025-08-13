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
    double tailLevel=0.1;
    boolean isDotted=false;
    StringBuilder suffix=new StringBuilder();
    @Override
    public boolean append(char c)
    {
        //TODO Restore sign or acknowledge its useless...
        //TODO bin/oct/hex
        if(Character.isLetter(c))
        {
            suffix.append(c);
            return true;
        }
        if(!suffix.isEmpty()){throw new InvalidTokenException(String.valueOf(abs+tail)+suffix+c+"...");}
        if(Character.isDigit(c))
        {
            if(!isDotted)
            {
                abs*=10;
                abs+=c-'0';
            }
            else
            {
                tail+=tailLevel*(c-'0');
                tailLevel/=10;
            }
            return true;
        }
        else if(c=='.')
        {
            isDotted=true;
        }
        return false;
    }
    @Override
    public AbstractToken build()
    {
        switch (suffix.toString().toLowerCase())
        {
            case "":
            {
                if(isDotted)return new NumberToken<>(abs + tail, DOUBLE,fileName, line, pos);
                else return new NumberToken<>(abs,INT,fileName,line,pos);
            }
            case "u":
            {
                if(isDotted)throw new InvalidTokenException("\"u\" cannot be applied to double");
                else return new NumberToken<>(abs,UNSIGNED_INT,fileName,line,pos);
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
                return new NumberToken<>(abs+tail,FLOAT,fileName,line,pos);
            }
            default:
            {
                throw new InvalidTokenException("Unknown suffix: "+suffix.toString().toLowerCase());
            }
        }
    }
}

package io.silvicky.novel.compiler.tokens;

import io.silvicky.novel.compiler.types.PrimitiveType;

import java.util.HashMap;
import java.util.Map;

public class CharTokenBuilder extends TokenBuilder
{
    private char val;
    private boolean isStarted=false;
    private boolean isEscape=false;
    private boolean isFinished=false;
    private boolean isEnclosed=false;
    private int base=0;
    private static final Map<Character,Character> escapeMap=new HashMap<>();
    static
    {
        escapeMap.put('\'','\'');
        escapeMap.put('\"','\"');
        escapeMap.put('\\','\\');
        escapeMap.put('?','?');
        escapeMap.put('n','\n');
        escapeMap.put('r','\r');
        escapeMap.put('t','\t');
        escapeMap.put('b','\b');
        escapeMap.put('f','\f');
        escapeMap.put('a','\007');
        escapeMap.put('v','\013');
    }
    public CharTokenBuilder(String fileName, int line, int pos)
    {
        super(fileName, line, pos);
    }
    @Override
    public boolean append(char c)
    {
        if(isEnclosed)return false;
        if(!isStarted)
        {
            if(c=='\'')
            {
                isStarted=true;
                return true;
            }
            throw new InvalidTokenException("invalid char");
        }
        if(isFinished)
        {
            if(c=='\'')
            {
                isEnclosed=true;
                return true;
            }
            throw new InvalidTokenException("invalid char");
        }
        if(isEscape)
        {
            if(c=='\''&&base!=0)
            {
                isFinished=true;
                isEnclosed=true;
                return true;
            }
            if(base==8)
            {
                if(c>'7'||c<'0')return false;
                val*=8;
                val+= (char) (c-'0');
                return true;
            }
            if(base==16)
            {
                c=Character.toLowerCase(c);
                if(c>='0'&&c<='9')
                {
                    val*=16;
                    val+=(char)(c-'0');
                    return true;
                }
                if(c>='a'&&c<='f')
                {
                    val*=16;
                    val+=(char)(c-'a'+10);
                    return true;
                }
                return false;
            }
            if(c=='x')
            {
                base=16;
                return true;
            }
            if(escapeMap.containsKey(c))
            {
                val=escapeMap.get(c);
                isFinished=true;
                return true;
            }
            base=8;
            if(c<'0'||c>'7')throw new InvalidTokenException("invalid char");
            val= (char) (c-'0');
        }
        else
        {
            if(c=='\\')
            {
                isEscape=true;
                return true;
            }
            val=c;
            isFinished=true;
        }
        return true;
    }
    @Override
    public AbstractToken build()
    {
        if(!isEnclosed)throw new InvalidTokenException("invalid char");
        if(val>=256)throw new InvalidTokenException("invalid C char");
        return new NumberToken<>((byte)val, PrimitiveType.CHAR,fileName,line,pos);
    }
}

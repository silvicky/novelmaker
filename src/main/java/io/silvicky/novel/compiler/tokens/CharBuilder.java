package io.silvicky.novel.compiler.tokens;

import java.util.HashMap;
import java.util.Map;

public class CharBuilder
{
    private char val;
    public boolean isEscape=false;
    private boolean isFinished=false;
    public boolean isReady=false;
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
    public boolean append(char c)
    {
        if(isFinished)return false;
        if(isEscape)
        {
            if(base==8)
            {
                if(c>'7'||c<'0')
                {
                    isFinished=true;
                    isReady=true;
                    return false;
                }
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
                isFinished=true;
                isReady=true;
                return false;
            }
            if(c=='x')
            {
                base=16;
                isReady=true;
                return true;
            }
            if(escapeMap.containsKey(c))
            {
                val=escapeMap.get(c);
                isFinished=true;
                isReady=true;
                return true;
            }
            base=8;
            if(c<'0'||c>'7')throw new InvalidTokenException("invalid char");
            isReady=true;
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
            isReady=true;
        }
        return true;
    }
    public char build()
    {
        if(!isReady)throw new InvalidTokenException("invalid char");
        return val;
    }
}

package io.silvicky.novel.compiler.tokens;

import io.silvicky.novel.compiler.types.Type;

public class NumberToken<T> extends Token
{
    public final T value;
    public final Type type;
    public NumberToken(){this(null,null);}
    public NumberToken(T value, Type type){super();this.value=value;this.type=type;}
    public NumberToken(T value, Type type, String fileName, int line, int pos)
    {
        super(fileName,line,pos);
        this.value = value;
        this.type=type;
    }
    @Override
    public String toString()
    {
        if(line==-1)
        {
            return String.format("'%s'",value.toString());
        }
        return String.format("'%s'@(%s,%d,%d)",value.toString(),fileName,line,pos);
    }
}

package io.silvicky.novel.compiler.tokens;

public class OperatorToken extends Token
{
    public final OperatorType type;
    public OperatorToken(OperatorType type){super();this.type=type;}
    public OperatorToken(OperatorType type, String fileName, int line, int pos)
    {
        super(fileName,line,pos);
        this.type = type;
    }
    @Override
    public String toString()
    {
        if(line==-1)
        {
            return String.format("'%s'",type.symbol);
        }
        return String.format("'%s'@(%s,%d,%d)",type.symbol,fileName,line,pos);
    }
}

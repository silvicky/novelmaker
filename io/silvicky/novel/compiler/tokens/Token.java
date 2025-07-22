package io.silvicky.novel.compiler.tokens;

public class Token implements AbstractToken
{
    public final String fileName;
    public final int line;
    public final int pos;
    public Token(){this("",-1,-1);}
    public Token(String fileName, int line, int pos)
    {
        this.fileName = fileName;
        this.line = line;
        this.pos = pos;
    }
}

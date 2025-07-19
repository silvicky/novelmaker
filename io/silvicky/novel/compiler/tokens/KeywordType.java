package io.silvicky.novel.compiler.tokens;

public enum KeywordType
{
    FOR("for"),
    WHILE("while"),
    DO("do"),
    IF("if"),
    ELSE("else"),
    RETURN("return"),
    TRUE("true"),
    FALSE("false"),
    INT("int"),
    VOID("void"),
    GOTO("goto"),
    BREAK("break"),
    CONTINUE("continue");
    public final String symbol;
    KeywordType(String symbol)
    {
        this.symbol=symbol;
    }
    public static KeywordType find(String s)
    {
        for(KeywordType type:KeywordType.values())
        {
            if(type.symbol.equals(s))return type;
        }
        return null;
    }
}

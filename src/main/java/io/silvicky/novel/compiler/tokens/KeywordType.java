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
    AND("and"),
    AND_EQ("and_eq"),
    BITAND("bitand"),
    BITOR("bitor"),
    COMPL("compl"),
    NOT("not"),
    NOT_EQ("not_eq"),
    OR("or"),
    OR_EQ("or_eq"),
    XOR("xor"),
    XOR_EQ("xor_eq"),
    INT("int"),
    CHAR("char"),
    BOOL("bool"),
    SHORT("short"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    UNSIGNED("unsigned"),
    SIGNED("signed"),
    CONST("const"),
    VOID("void"),
    AUTO("auto"),
    GOTO("goto"),
    BREAK("break"),
    CONTINUE("continue"),
    SWITCH("switch"),
    CASE("case"),
    DEFAULT("default"),
    DECLTYPE("decltype"),//TODO
    STRUCT("struct"),//TODO
    CLASS("class"),//TODO
    ENUM("enum"),//TODO
    UNION("union"),//TODO
    TYPEDEF("typedef"),//TODO
    STATIC("static"),//TODO
    SIZEOF("sizeof");
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

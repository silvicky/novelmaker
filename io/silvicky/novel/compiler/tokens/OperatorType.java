package io.silvicky.novel.compiler.tokens;

public enum OperatorType
{
    L_PARENTHESES("(",OperatorArgsProperties.INVALID,null),
    R_PARENTHESES(")",OperatorArgsProperties.INVALID,null),
    L_BRACKET("[",OperatorArgsProperties.INVALID,null),
    R_BRACKET("]",OperatorArgsProperties.INVALID,null),
    L_BRACE("{",OperatorArgsProperties.INVALID,null),
    R_BRACE("}",OperatorArgsProperties.INVALID,null),
    EQUAL("=",OperatorArgsProperties.BINARY,(a,b,c)->b),
    LESS("<",OperatorArgsProperties.BINARY,(a,b,c)->((a<b)?1:0)),
    GREATER(">",OperatorArgsProperties.BINARY,(a,b,c)->((a>b)?1:0)),
    LESS_EQUAL("<=",OperatorArgsProperties.BINARY,(a,b,c)->((a<=b)?1:0)),
    GREATER_EQUAL(">=",OperatorArgsProperties.BINARY,(a,b,c)->((a>=b)?1:0)),
    NOT_EQUAL("!=",OperatorArgsProperties.BINARY,(a,b,c)->((a!=b)?1:0)),
    EQUAL_EQUAL("==",OperatorArgsProperties.BINARY,(a,b,c)->((a==b)?1:0)),
    L_SHIFT("<<",OperatorArgsProperties.BINARY,(a,b,c)->a<<b),
    R_SHIFT(">>",OperatorArgsProperties.BINARY,(a,b,c)->a>>b),
    L_SHIFT_EQUAL("<<=",OperatorArgsProperties.BINARY,(a,b,c)->a<<b),
    R_SHIFT_EQUAL(">>=",OperatorArgsProperties.BINARY,(a,b,c)->a>>b),
    NOT("!",OperatorArgsProperties.UNARY_R,(a,b,c)->(a==0)?1:0),
    COLON(":",OperatorArgsProperties.TERNARY,(a,b,c)->(a==1)?b:c),
    SEMICOLON(";",OperatorArgsProperties.INVALID,null),
    COMMA(",",OperatorArgsProperties.BINARY,(a,b,c)->b),
    DOT(".",OperatorArgsProperties.INVALID,null),
    MULTIPLY("*",OperatorArgsProperties.BINARY,(a,b,c)->a*b),
    MULTIPLY_EQUAL("*=",OperatorArgsProperties.BINARY,(a,b,c)->a*b),
    PLUS("+",OperatorArgsProperties.BINARY,(a,b,c)->a+b),
    PLUS_EQUAL("+=",OperatorArgsProperties.BINARY,(a,b,c)->a+b),
    PLUS_PLUS("++",OperatorArgsProperties.UNARY,(a,b,c)->a+c),
    MINUS("-",OperatorArgsProperties.BINARY,(a,b,c)->a-b),
    MINUS_EQUAL("-=",OperatorArgsProperties.BINARY,(a,b,c)->a-b),
    MINUS_MINUS("--",OperatorArgsProperties.UNARY,(a,b,c)->a-c),
    DIVIDE("/",OperatorArgsProperties.BINARY,(a,b,c)->a/b),
    DIVIDE_EQUAL("/=",OperatorArgsProperties.BINARY,(a,b,c)->a/b),
    BACKSLASH("\\",OperatorArgsProperties.INVALID,null),
    REVERSE("~",OperatorArgsProperties.UNARY_R,(a,b,c)->~a),
    OR("|",OperatorArgsProperties.BINARY,(a,b,c)->a|b),
    OR_EQUAL("|=",OperatorArgsProperties.BINARY,(a,b,c)->a|b),
    OR_OR("||",OperatorArgsProperties.BINARY,(a,b,c)->((a!=0)||(b!=0))?1:0),
    OR_OR_EQUAL("||=",OperatorArgsProperties.BINARY,(a,b,c)->((a!=0)||(b!=0))?1:0),
    AND("&",OperatorArgsProperties.BINARY,(a,b,c)->a&b),
    AND_EQUAL("&=",OperatorArgsProperties.BINARY,(a,b,c)->a&b),
    AND_AND("&&",OperatorArgsProperties.BINARY,(a,b,c)->((a!=0)&&(b!=0))?1:0),
    AND_AND_EQUAL("&&=",OperatorArgsProperties.BINARY,(a,b,c)->((a!=0)&&(b!=0))?1:0),
    XOR("^",OperatorArgsProperties.BINARY,(a,b,c)->a^b),
    XOR_EQUAL("^=",OperatorArgsProperties.BINARY,(a,b,c)->a^b),
    ;
    public static enum OperatorArgsProperties
    {
        UNARY_R,
        UNARY,
        BINARY,
        TERNARY,
        INVALID
    }
    public final String symbol;
    public final OperatorArgsProperties properties;
    public final OperatorOperation operation;
    OperatorType(String symbol, OperatorArgsProperties properties, OperatorOperation operation)
    {
        this.symbol=symbol;
        this.properties=properties;
        this.operation=operation;
    }
    public static OperatorType find(String s)
    {
        for(OperatorType type:OperatorType.values())
        {
            if(type.symbol.equals(s))return type;
        }
        return null;
    }
}

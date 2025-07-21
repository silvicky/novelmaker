package io.silvicky.novel.compiler.tokens;

public enum OperatorType
{
    L_PARENTHESES("("),
    R_PARENTHESES(")"),
    L_BRACKET("["),
    R_BRACKET("]"),
    L_BRACE("{"),
    R_BRACE("}"),
    LESS("<",OperatorArgsProperties.BINARY,(a,b,c)->((a<b)?1:0)),
    GREATER(">",OperatorArgsProperties.BINARY,(a,b,c)->((a>b)?1:0)),
    LESS_EQUAL("<=",OperatorArgsProperties.BINARY,(a,b,c)->((a<=b)?1:0)),
    GREATER_EQUAL(">=",OperatorArgsProperties.BINARY,(a,b,c)->((a>=b)?1:0)),
    NOT_EQUAL("!=",OperatorArgsProperties.BINARY,(a,b,c)->((a!=b)?1:0)),
    EQUAL_EQUAL("==",OperatorArgsProperties.BINARY,(a,b,c)->((a==b)?1:0)),
    L_SHIFT("<<",OperatorArgsProperties.BINARY,(a,b,c)->a<<b),
    R_SHIFT(">>",OperatorArgsProperties.BINARY,(a,b,c)->a>>b),
    L_SHIFT_EQUAL("<<=",OperatorArgsProperties.BINARY_ASSIGN,L_SHIFT),
    R_SHIFT_EQUAL(">>=",OperatorArgsProperties.BINARY_ASSIGN,R_SHIFT),
    NOT("!",OperatorArgsProperties.UNARY_R,(a,b,c)->(a==0)?1:0),
    COLON(":"),
    QUESTION("?",OperatorArgsProperties.TERNARY,(a,b,c)->(a==1)?b:c),
    LABEL(":;"),
    SEMICOLON(";"),
    COMMA(",",OperatorArgsProperties.BINARY,(a,b,c)->b),
    DOT("."),
    MULTIPLY("*",OperatorArgsProperties.BINARY,(a,b,c)->a*b),
    MULTIPLY_EQUAL("*=",OperatorArgsProperties.BINARY_ASSIGN,MULTIPLY),
    PLUS("+",OperatorArgsProperties.BINARY,(a,b,c)->a+b),
    PLUS_EQUAL("+=",OperatorArgsProperties.BINARY_ASSIGN,PLUS),
    PLUS_PLUS("++",OperatorArgsProperties.UNARY,PLUS),
    MINUS("-",OperatorArgsProperties.BINARY,(a,b,c)->a-b),
    MINUS_EQUAL("-=",OperatorArgsProperties.BINARY_ASSIGN,MINUS),
    MINUS_MINUS("--",OperatorArgsProperties.UNARY,MINUS),
    DIVIDE("/",OperatorArgsProperties.BINARY,(a,b,c)->a/b),
    DIVIDE_EQUAL("/=",OperatorArgsProperties.BINARY_ASSIGN,DIVIDE),
    MOD("%",OperatorArgsProperties.BINARY,(a,b,c)->a%b),
    MOD_EQUAL("%=",OperatorArgsProperties.BINARY_ASSIGN,MOD),
    BACKSLASH("\\"),
    REVERSE("~",OperatorArgsProperties.UNARY_R,(a,b,c)->~a),
    OR("|",OperatorArgsProperties.BINARY,(a,b,c)->a|b),
    OR_EQUAL("|=",OperatorArgsProperties.BINARY_ASSIGN,OR),
    OR_OR("||",OperatorArgsProperties.BINARY,(a,b,c)->((a!=0)||(b!=0))?1:0),
    OR_OR_EQUAL("||=",OperatorArgsProperties.BINARY_ASSIGN,OR_OR),
    AND("&",OperatorArgsProperties.BINARY,(a,b,c)->a&b),
    AND_EQUAL("&=",OperatorArgsProperties.BINARY_ASSIGN,AND),
    AND_AND("&&",OperatorArgsProperties.BINARY,(a,b,c)->((a!=0)&&(b!=0))?1:0),
    AND_AND_EQUAL("&&=",OperatorArgsProperties.BINARY_ASSIGN,AND_AND),
    XOR("^",OperatorArgsProperties.BINARY,(a,b,c)->a^b),
    XOR_EQUAL("^=",OperatorArgsProperties.BINARY_ASSIGN,XOR),
    NOP("",OperatorArgsProperties.UNARY_R,(a,b,c)->a),
    EQUAL("=",OperatorArgsProperties.BINARY_ASSIGN,COMMA)
    ;
    public enum OperatorArgsProperties
    {
        UNARY_R,
        UNARY,
        BINARY,
        TERNARY,
        INVALID,
        BINARY_ASSIGN
    }
    public final String symbol;
    public final OperatorArgsProperties properties;
    public final OperatorOperation operation;
    public final OperatorType baseType;
    OperatorType(String symbol, OperatorArgsProperties properties, OperatorOperation operation, OperatorType baseType)
    {
        this.symbol=symbol;
        this.properties=properties;
        this.operation=operation;
        this.baseType=baseType;
    }
    OperatorType(String symbol, OperatorArgsProperties properties, OperatorType baseType)
    {this(symbol,properties, baseType.operation,baseType);}
    OperatorType(String symbol, OperatorArgsProperties properties, OperatorOperation operation)
    {this(symbol,properties,operation,null);}
    OperatorType(String symbol)
    {this(symbol,OperatorArgsProperties.INVALID,null,null);}
    public static OperatorType find(String s)
    {
        for(OperatorType type:OperatorType.values())
        {
            if(type.symbol.equals(s))return type;
        }
        return null;
    }
}

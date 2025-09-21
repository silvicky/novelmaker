package io.silvicky.novel.compiler.tokens;

public enum OperatorType
{
    L_PARENTHESES("("),
    R_PARENTHESES(")"),
    L_BRACKET("["),
    ALT_L_BRACKET("<:"),
    R_BRACKET("]"),
    ALT_R_BRACKET(":>"),
    L_BRACE("{"),
    ALT_L_BRACE("<%"),
    R_BRACE("}"),
    ALT_R_BRACE("%>"),
    LESS("<",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case BOOL ->
            {
                return (!(boolean)a)&&(boolean)b;
            }
            case CHAR ->
            {
                return (byte) a < (byte) b;
            }
            case UNSIGNED_CHAR ->
            {
                return Byte.compareUnsigned((byte)a,(byte)b)<0;
            }
            case SHORT ->
            {
                return (short)a<(short) b;
            }
            case UNSIGNED_SHORT ->
            {
                return Short.compareUnsigned((short) a,(short) b)<0;
            }
            case INT,LONG ->
            {
                return (int)a<(int) b;
            }
            case UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return Integer.compareUnsigned((int) a,(int) b)<0;
            }
            case LONG_LONG ->
            {
                return (long)a<(long) b;
            }
            case UNSIGNED_LONG_LONG ->
            {
                return Long.compareUnsigned((long)a,(long)b)<0;
            }
            case FLOAT ->
            {
                return (float)a<(float) b;
            }
            case DOUBLE,LONG_DOUBLE ->
            {
                return (double)a<(double) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    GREATER(">",OperatorArgsProperties.BINARY,(a,b,t)->LESS.operation.cal(b,a,t)),
    LESS_EQUAL("<=",OperatorArgsProperties.BINARY,(a,b,t)-> !(boolean)(LESS.operation.cal(b,a,t))),
    GREATER_EQUAL(">=",OperatorArgsProperties.BINARY,(a,b,t)-> !(boolean)(LESS.operation.cal(a,b,t))),
    NOT_EQUAL("!=",OperatorArgsProperties.BINARY,(a,b,t)->(boolean)(LESS.operation.cal(a,b,t))||(boolean)(LESS.operation.cal(b,a,t))),
    EQUAL_EQUAL("==",OperatorArgsProperties.BINARY,(a,b,t)->!((boolean)(LESS.operation.cal(a,b,t))||(boolean)(LESS.operation.cal(b,a,t)))),
    L_SHIFT("<<",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte) a << (byte) b;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)a<<(short) b;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)a<<(int) b;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return (long)a<<(long) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    R_SHIFT(">>",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte) a >> (byte) b;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)a>>(short) b;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)a>>(int) b;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return (long)a>>(long) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    L_SHIFT_EQUAL("<<=",OperatorArgsProperties.BINARY_ASSIGN,L_SHIFT),
    R_SHIFT_EQUAL(">>=",OperatorArgsProperties.BINARY_ASSIGN,R_SHIFT),
    NOT("!",OperatorArgsProperties.UNARY_R,(a,b,t)->
    {
        switch (t)
        {
            case BOOL ->
            {
                return !(boolean) a;
            }
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte) a ==0;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)a==0;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)a==0;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return (long)a==0;
            }
            case FLOAT ->
            {
                return (float)a==0;
            }
            case DOUBLE,LONG_DOUBLE ->
            {
                return (double)a==0;
            }
            default ->
            {
                return null;
            }
        }
    }),
    COLON(":"),
    QUESTION("?"),
    SEMICOLON(";"),
    COMMA(",",OperatorArgsProperties.BINARY,(a,b,t)->b),
    DOT("."),
    MULTIPLY("*",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte) a * (byte) b;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)a*(short) b;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)a*(int) b;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return (long)a*(long) b;
            }
            case FLOAT ->
            {
                return (float)a*(float) b;
            }
            case DOUBLE,LONG_DOUBLE ->
            {
                return (double)a*(double) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    MULTIPLY_EQUAL("*=",OperatorArgsProperties.BINARY_ASSIGN,MULTIPLY),
    PLUS("+",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte) a + (byte) b;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)a+(short) b;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)a+(int) b;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return (long)a+(long) b;
            }
            case FLOAT ->
            {
                return (float)a+(float) b;
            }
            case DOUBLE,LONG_DOUBLE ->
            {
                return (double)a+(double) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    PLUS_EQUAL("+=",OperatorArgsProperties.BINARY_ASSIGN,PLUS),
    PLUS_PLUS("++",OperatorArgsProperties.UNARY,PLUS),
    MINUS("-",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte) a - (byte) b;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)a-(short) b;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)a-(int) b;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return (long)a-(long) b;
            }
            case FLOAT ->
            {
                return (float)a-(float) b;
            }
            case DOUBLE,LONG_DOUBLE ->
            {
                return (double)a-(double) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    MINUS_EQUAL("-=",OperatorArgsProperties.BINARY_ASSIGN,MINUS),
    MINUS_MINUS("--",OperatorArgsProperties.UNARY,MINUS),
    DIVIDE("/",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case CHAR ->
            {
                return (byte) a / (byte) b;
            }
            case SHORT ->
            {
                return (short)a/(short) b;
            }
            case INT,LONG ->
            {
                return (int)a/(int) b;
            }
            case UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return Integer.divideUnsigned((int) a,(int) b);
            }
            case LONG_LONG ->
            {
                return (long)a/(long) b;
            }
            case UNSIGNED_LONG_LONG ->
            {
                return Long.divideUnsigned((long)a,(long)b);
            }
            case FLOAT ->
            {
                return (float)a/(float) b;
            }
            case DOUBLE,LONG_DOUBLE ->
            {
                return (double)a/(double) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    DIVIDE_EQUAL("/=",OperatorArgsProperties.BINARY_ASSIGN,DIVIDE),
    MOD("%",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case CHAR ->
            {
                return (byte) a % (byte) b;
            }
            case SHORT ->
            {
                return (short)a%(short) b;
            }
            case INT,LONG ->
            {
                return (int)a%(int) b;
            }
            case UNSIGNED_INT,UNSIGNED_LONG->
            {
                return Integer.remainderUnsigned((int)a,(int)b);
            }
            case LONG_LONG ->
            {
                return (long)a%(long) b;
            }
            case UNSIGNED_LONG_LONG->
            {
                return Long.remainderUnsigned((long)a,(long)b);
            }
            default ->
            {
                return null;
            }
        }
    }),
    MOD_EQUAL("%=",OperatorArgsProperties.BINARY_ASSIGN,MOD),
    BACKSLASH("\\"),
    REVERSE("~",OperatorArgsProperties.UNARY_R,(a,b,t)->
    {
        switch (t)
        {
            case BOOL ->
            {
                return !(boolean)a;
            }
            case CHAR,UNSIGNED_CHAR ->
            {
                return ~(byte) a;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return ~(short)a;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return ~(int)a;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return ~(long)a;
            }
            default ->
            {
                return null;
            }
        }
    }),
    OR("|",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case BOOL ->
            {
                return (boolean)a||(boolean)b;
            }
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte) a | (byte) b;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)a|(short) b;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)a|(int) b;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return (long)a|(long) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    OR_EQUAL("|=",OperatorArgsProperties.BINARY_ASSIGN,OR),
    OR_OR("||"),
    AND("&",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case BOOL ->
            {
                return (boolean)a&&(boolean)b;
            }
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte) a & (byte) b;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)a&(short) b;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)a&(int) b;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return (long)a&(long) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    AND_EQUAL("&=",OperatorArgsProperties.BINARY_ASSIGN,AND),
    AND_AND("&&"),
    XOR("^",OperatorArgsProperties.BINARY,(a,b,t)->
    {
        switch (t)
        {
            case BOOL ->
            {
                return (boolean)a^(boolean)b;
            }
            case CHAR,UNSIGNED_CHAR ->
            {
                return (byte) a ^ (byte) b;
            }
            case SHORT,UNSIGNED_SHORT ->
            {
                return (short)a^(short) b;
            }
            case INT,LONG,UNSIGNED_INT,UNSIGNED_LONG ->
            {
                return (int)a^(int) b;
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return (long)a^(long) b;
            }
            default ->
            {
                return null;
            }
        }
    }),
    XOR_EQUAL("^=",OperatorArgsProperties.BINARY_ASSIGN,XOR),
    NOP("",OperatorArgsProperties.UNARY_R,(a,b,t)->a),
    EQUAL("=",OperatorArgsProperties.BINARY_ASSIGN,COMMA),
    SHARP("#"),
    SHARP_SHARP("##"),
    ALT_SHARP("%:"),
    ALT_SHARP_SHARP("%:%:"),
    ALT_SHARP_MOD("%:%"),
    ;
    public enum OperatorArgsProperties
    {
        UNARY_R,
        UNARY,
        BINARY,
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

package io.silvicky.novel.compiler.tokens;

public enum OperatorType
{
    L_PARENTHESES("("),
    R_PARENTHESES(")"),
    L_BRACKET("["),
    R_BRACKET("]"),
    L_BRACE("{"),
    R_BRACE("}"),
    EQUAL("="),
    LESS("<"),
    GREATER(">"),
    LESS_EQUAL("<="),
    GREATER_EQUAL(">="),
    NOT_EQUAL("!="),
    EQUAL_EQUAL("=="),
    L_SHIFT("<<"),
    R_SHIFT(">>"),
    L_SHIFT_EQUAL("<<="),
    R_SHIFT_EQUAL(">>="),
    NOT("!"),
    COLON(":"),
    SEMICOLON(";"),
    COMMA(","),
    DOT("."),
    MULTIPLY("*"),
    MULTIPLY_EQUAL("*="),
    PLUS("+"),
    PLUS_EQUAL("+="),
    PLUS_PLUS("++"),
    MINUS("-"),
    MINUS_EQUAL("-="),
    MINUS_MINUS("--"),
    DIVIDE("/"),
    DIVIDE_EQUAL("/="),
    BACKSLASH("\\"),
    REVERSE("~");
    public final String symbol;
    OperatorType(String symbol)
    {
        this.symbol=symbol;
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

package io.silvicky.novel.compiler.tokens;

public enum TokenType
{
    IDENTIFIER(IdentifierToken.class),
    NUMBER(NumberToken.class),
    OPERATOR(OperatorToken.class);
    public final Class<?> cls;
    TokenType(Class<?> cls)
    {
        this.cls=cls;
    }
}

package io.silvicky.novel.compiler.tokens;

public enum PreprocessorToken implements AbstractToken
{
    EOF(""),
    EOL(""),
    SHARP("#"),
    SHARP_SHARP("##");
    public final String symbol;

    PreprocessorToken(String symbol)
    {
        this.symbol = symbol;
    }
}

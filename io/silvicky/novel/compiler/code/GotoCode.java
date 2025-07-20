package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.tokens.OperatorType;

public record GotoCode(long left, long right, OperatorType op, String id) implements Code
{
}

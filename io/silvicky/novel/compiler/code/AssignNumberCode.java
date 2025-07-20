package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.tokens.OperatorType;

public record AssignNumberCode(long target, long left) implements Code
{
}

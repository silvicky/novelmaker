package io.silvicky.novel.compiler.code;

import io.silvicky.novel.compiler.tokens.OperatorType;

public record AssignCode(long target, long left, long right, OperatorType op) implements Code
{
}

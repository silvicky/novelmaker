package io.silvicky.novel.compiler.code;

public record ReturnCode() implements Code
{
    @Override
    public String toString(){return "RET";}
}

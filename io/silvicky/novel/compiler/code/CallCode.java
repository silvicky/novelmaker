package io.silvicky.novel.compiler.code;

import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupLabelName;

public record CallCode(int target, List<Integer> parameters) implements Code
{
    @Override
    public String toString(){return String.format("%s(%s)",lookupLabelName(target),parameters.toString());}
}

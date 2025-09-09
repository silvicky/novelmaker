package io.silvicky.novel.compiler.code;

import static io.silvicky.novel.compiler.Compiler.lookupLabelName;
import static io.silvicky.novel.compiler.Compiler.requestLabel;

public record LabelCode(int id) implements Code
{
    public LabelCode(){this(requestLabel());}

    @Override
    public String toString()
    {
        return lookupLabelName(id)+":";
    }
}

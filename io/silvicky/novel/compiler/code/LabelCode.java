package io.silvicky.novel.compiler.code;

import static io.silvicky.novel.compiler.Compiler.lookupLabelName;
import static io.silvicky.novel.compiler.Compiler.requestInternalLabel;

public record LabelCode(long id) implements Code
{
    public LabelCode(){this(requestInternalLabel());}
    public LabelCode(long id){this.id=id;}

    @Override
    public String toString()
    {
        return lookupLabelName(id)+":";
    }
}

package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.declaration.DeclarationRoot;
import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Program extends NonTerminal
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next==null||next instanceof EofToken)return ret;
        DeclarationRoot declarationRoot =new DeclarationRoot(null,false);
        Program program=new Program();
        ret.add(new AppendCodeSeqOperation(this,program));
        ret.add(program);
        ret.add(new AppendCodeSeqOperation(this, declarationRoot));
        ret.add(declarationRoot);
        return ret;
    }
}

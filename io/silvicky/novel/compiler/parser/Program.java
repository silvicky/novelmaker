package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.declaration.DeclarationRoot;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.EofToken;

import java.util.ArrayList;
import java.util.List;

public class Program extends NonTerminal implements ASTNode
{
    private DeclarationRoot declarationRoot=null;
    private Program program=null;
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next==null||next instanceof EofToken)return ret;
        declarationRoot =new DeclarationRoot(null);
        program=new Program();
        ret.add(program);
        ret.add(declarationRoot);
        return ret;
    }

    @Override
    public void travel()
    {
        if(declarationRoot==null)return;
        declarationRoot.travel();
        codes.addAll(declarationRoot.codes);
        program.travel();
        codes.addAll(program.codes);
    }
}

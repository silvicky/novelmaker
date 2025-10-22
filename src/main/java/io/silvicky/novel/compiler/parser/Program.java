package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.declaration.DeclarationRoot;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.PreprocessorToken;

import java.util.ArrayList;
import java.util.List;

public class Program extends NonTerminal implements ASTNode
{
    private DeclarationRoot declarationRoot=null;
    private Program program=null;
    private final NonTerminal directParent;

    public Program(NonTerminal directParent)
    {
        this.directParent = directParent;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next==null||next== PreprocessorToken.EOF||(next instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.R_BRACE))return ret;
        declarationRoot =new DeclarationRoot(directParent);
        program=new Program(directParent);
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

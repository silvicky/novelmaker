package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.PreprocessorToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.silvicky.novel.compiler.Compiler.revokeLocalVariable;

public class Block extends NonTerminal implements ASTNode
{
    public final int breakLabel,continueLabel;
    public final NonTerminal directParent;
    private Line current=null;
    private Block residue=null;
    public Block(int breakLabel,int continueLabel,NonTerminal directParent){this.breakLabel=breakLabel;this.continueLabel=continueLabel;this.directParent=directParent;}
    public Block(){this(-1,-1,null);}
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken&&((OperatorToken) next).type== OperatorType.R_BRACE)return ret;
        if(next== PreprocessorToken.EOF)return ret;
        residue=new Block(breakLabel,continueLabel, Objects.requireNonNullElse(this.directParent,this));
        current=new Line(breakLabel,continueLabel,Objects.requireNonNullElse(this.directParent,this));
        ret.add(residue);
        ret.add(current);
        return ret;
    }

    @Override
    public void travel()
    {
        if(current!=null)
        {
            current.travel();
            residue.travel();
            codes.addAll(current.codes);
            codes.addAll(residue.codes);
            for(String s:revokedVariables)revokeLocalVariable(s);
        }
    }
}

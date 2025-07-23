package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.tokens.EofToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Block extends NonTerminal
{
    public final int breakLabel,continueLabel;
    public final NonTerminal directParent;
    public Block(int breakLabel,int continueLabel,NonTerminal directParent){this.breakLabel=breakLabel;this.continueLabel=continueLabel;this.directParent=directParent;}
    public Block(){this(-1,-1,null);}
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken&&((OperatorToken) next).type== OperatorType.R_BRACE)return ret;
        if(next instanceof EofToken)return ret;
        Block residue=new Block(breakLabel,continueLabel, Objects.requireNonNullElse(this.directParent,this));
        Line current=new Line(breakLabel,continueLabel,Objects.requireNonNullElse(this.directParent,this));
        ret.add(new AppendCodeSeqOperation(this,residue));
        ret.add(residue);
        ret.add(new AppendCodeSeqOperation(this,current));
        ret.add(current);
        return ret;
    }
}

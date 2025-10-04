package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;

import java.util.ArrayList;
import java.util.List;

public class ListEntriesResidue implements AbstractJsonEntity
{
    private final ListEntity root;

    public ListEntriesResidue(ListEntity root)
    {
        this.root = root;
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type== OperatorType.R_BRACKET)return ret;
        ret.add(new ListEntries(root));
        ret.add(new OperatorToken(OperatorType.COMMA));
        return ret;
    }
}

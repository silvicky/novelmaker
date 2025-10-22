package io.silvicky.novel.compiler.parser.declaration;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.Program;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.Type;
import io.silvicky.novel.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class DeclarationBlock extends NonTerminal implements ASTNode
{
    public final List<Pair<String, Type>> fields=new ArrayList<>();
    private Program program;
    @Override
    public void travel()
    {
        program.travel();
    }

    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        program=new Program(this);
        ret.add(new OperatorToken(OperatorType.R_BRACE));
        ret.add(program);
        ret.add(new OperatorToken(OperatorType.L_BRACE));
        return ret;
    }
}

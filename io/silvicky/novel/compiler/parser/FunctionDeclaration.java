package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.ReturnCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.parser.operation.AppendCodeOperation;
import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.parser.operation.ResetCtxOperation;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.tokens.AbstractToken;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.*;

public class FunctionDeclaration extends NonTerminal
{
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        if(!(next instanceof IdentifierToken identifierToken))throw new GrammarException(this.getClass().getSimpleName()+next+second);
        List<AbstractToken> ret=new ArrayList<>();
        ctx=registerLabel(identifierToken.id);
        int endLabel=requestLabel();
        Block block=new Block();
        ret.add(new ResetCtxOperation());
        ret.add(new AppendCodeOperation(this,new LabelCode(endLabel)));
        ret.add(new AppendCodeOperation(this,new ReturnCode()));
        ret.add(new AppendCodeSeqOperation(this,block));
        ret.add(new OperatorToken(OperatorType.R_BRACE));
        ret.add(block);
        ret.add(new AppendCodeOperation(this,new LabelCode(ctx)));
        ret.add(new AppendCodeOperation(this,new UnconditionalGotoCode(endLabel)));
        ret.add(new OperatorToken(OperatorType.L_BRACE));
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(new Arguments());
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        ret.add(new IdentifierToken(identifierToken.id));
        return ret;
    }
}

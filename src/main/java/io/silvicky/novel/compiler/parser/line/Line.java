package io.silvicky.novel.compiler.parser.line;

import io.silvicky.novel.compiler.parser.ASTNode;
import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.declaration.DeclarationRoot;
import io.silvicky.novel.compiler.parser.expression.ExpressionRoot;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.silvicky.novel.compiler.Compiler.revokeLocalVariable;

public class Line extends NonTerminal implements ASTNode
{
    public final int breakLabel,continueLabel;
    public final NonTerminal directParent;
    private NonTerminal content=null;
    public Line(int breakLabel,int continueLabel, NonTerminal directParent){this.breakLabel=breakLabel;this.continueLabel=continueLabel;this.directParent=directParent;}
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.L_BRACE)
        {
            content = new Block(breakLabel, continueLabel, Objects.requireNonNullElse(this.directParent,this));
            ret.add(new OperatorToken(OperatorType.R_BRACE));
            ret.add(content);
            ret.add(new OperatorToken(OperatorType.L_BRACE));
            return ret;
        }
        if(next instanceof KeywordToken)
        {
            KeywordType type=((KeywordToken) next).type;
            if(type==KeywordType.FOR)
            {
                content=new ForLine();
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.WHILE)
            {
                content=new WhileLine();
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.DO)
            {
                content=new DoWhileLine();
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.IF)
            {
                content=new IfLine(breakLabel,continueLabel);
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.BREAK)
            {
                if(breakLabel==-1)throw new GrammarException("break not in a loop");
                content=new BreakLine(breakLabel);
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.CONTINUE)
            {
                if(continueLabel==-1)throw new GrammarException("continue not in a loop");
                content=new ContinueLine(continueLabel);
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.GOTO)
            {
                content=new GotoLine();
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.RETURN)
            {
                content=new ReturnLine();
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.SWITCH)
            {
                content=new SwitchLine();
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.DEFAULT)
            {
                if(!(directParent instanceof SwitchLine switchLine))throw new GrammarException("not in a switch");
                content=new DefaultLine(switchLine);
                ret.add(content);
                return ret;
            }
            if(type==KeywordType.CASE)
            {
                if(!(directParent instanceof SwitchLine switchLine))throw new GrammarException("not in a switch");
                content=new CaseLine(switchLine);
                ret.add(content);
                return ret;
            }
            content=new DeclarationRoot(Objects.requireNonNullElse(this.directParent, this));
            ret.add(content);
            return ret;
        }
        else if(second instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.COLON)
        {
            if(!(next instanceof IdentifierToken identifierToken))
            {
                throw new GrammarException("label not named with an identifier");
            }
            content=new LabelLine(identifierToken.id);
            ret.add(content);
            return ret;
        }
        else
        {
            content=new ExpressionRoot();
            ret.add(new OperatorToken(OperatorType.SEMICOLON));
            ret.add(content);
            return ret;
        }
    }

    @Override
    public void travel()
    {
        if(content instanceof ASTNode astNode)astNode.travel();
        if(content!=null)codes.addAll(content.codes);
        for(String s:revokedVariables)revokeLocalVariable(s);
    }
}

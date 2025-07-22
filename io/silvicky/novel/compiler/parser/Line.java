package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.code.LabelCode;
import io.silvicky.novel.compiler.code.UnconditionalGotoCode;
import io.silvicky.novel.compiler.parser.operation.AppendCodeOperation;
import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.parser.operation.AppendExpressionGotoCodeOperation;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupLabel;

public class Line extends NonTerminal
{
    public final int breakLabel,continueLabel;
    public Line(int breakLabel,int continueLabel){this.breakLabel=breakLabel;this.continueLabel=continueLabel;}
    public Line(){this(-1,-1);}
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.L_BRACE)
        {
            Block block=new Block(breakLabel,continueLabel);
            ret.add(new AppendCodeSeqOperation(this,block));
            ret.add(new OperatorToken(OperatorType.R_BRACE));
            ret.add(block);
            ret.add(new OperatorToken(OperatorType.L_BRACE));
            return ret;
        }
        if(next instanceof KeywordToken)
        {
            KeywordType type=((KeywordToken) next).type;
            if(type==KeywordType.FOR)
            {
                LabelCode head=new LabelCode();
                LabelCode cont=new LabelCode();
                LabelCode end=new LabelCode();
                Line line=new Line(end.id(),cont.id());
                Expression first=new Expression();
                Expression expression=new Expression();
                Expression third=new Expression();
                ret.add(new AppendCodeOperation(this,end));
                ret.add(new AppendCodeOperation(this,new UnconditionalGotoCode(head.id())));
                ret.add(new AppendCodeSeqOperation(this,third));
                ret.add(new AppendCodeOperation(this,cont));
                ret.add(new AppendCodeSeqOperation(this,line));
                ret.add(new AppendExpressionGotoCodeOperation(this,expression,end.id(),OperatorType.NOT));
                ret.add(new AppendCodeSeqOperation(this,expression));
                ret.add(new AppendCodeOperation(this,head));
                ret.add(new AppendCodeSeqOperation(this,first));
                ret.add(line);
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(third);
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                ret.add(expression);
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                ret.add(first);
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                ret.add(new KeywordToken(KeywordType.FOR));
                return ret;
            }
            if(type==KeywordType.WHILE)
            {
                LabelCode head=new LabelCode();
                LabelCode end=new LabelCode();
                Line line=new Line(end.id(),head.id());
                Expression expression=new Expression();
                ret.add(new AppendCodeOperation(this,end));
                ret.add(new AppendCodeOperation(this,new UnconditionalGotoCode(head.id())));
                ret.add(new AppendCodeSeqOperation(this,line));
                ret.add(new AppendExpressionGotoCodeOperation(this,expression,end.id(),OperatorType.NOT));
                ret.add(new AppendCodeSeqOperation(this,expression));
                ret.add(new AppendCodeOperation(this,head));
                ret.add(line);
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(expression);
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                ret.add(new KeywordToken(KeywordType.WHILE));
                return ret;
            }
            if(type==KeywordType.DO)
            {
                LabelCode head=new LabelCode();
                LabelCode end=new LabelCode();
                LabelCode cont=new LabelCode();
                Line line=new Line(end.id(),cont.id());
                Expression expression=new Expression();
                ret.add(new AppendCodeOperation(this,end));
                ret.add(new AppendExpressionGotoCodeOperation(this,expression,head.id(),OperatorType.NOP));
                ret.add(new AppendCodeSeqOperation(this,expression));
                ret.add(new AppendCodeOperation(this,cont));
                ret.add(new AppendCodeSeqOperation(this,line));
                ret.add(new AppendCodeOperation(this,head));
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(expression);
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                ret.add(new KeywordToken(KeywordType.WHILE));
                ret.add(line);
                ret.add(new KeywordToken(KeywordType.DO));
                return ret;
            }
            if(type == KeywordType.INT)
            {
                ret.add(new VariableDeclaration());
                ret.add(new KeywordToken(KeywordType.INT));
                return ret;
            }
            if(type==KeywordType.IF)
            {
                LabelCode end=new LabelCode();
                Line line=new Line(breakLabel,continueLabel);
                Expression expression=new Expression();
                Else els=new Else(breakLabel,continueLabel);
                LabelCode elseLabel=new LabelCode();
                ret.add(new AppendCodeOperation(this,end));
                ret.add(new AppendCodeSeqOperation(this,els));
                ret.add(new AppendCodeOperation(this,elseLabel));
                ret.add(new AppendCodeOperation(this,new UnconditionalGotoCode(end.id())));
                ret.add(new AppendCodeSeqOperation(this,line));
                ret.add(new AppendExpressionGotoCodeOperation(this,expression,elseLabel.id(),OperatorType.NOT));
                ret.add(new AppendCodeSeqOperation(this,expression));
                ret.add(els);
                ret.add(line);
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(expression);
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                ret.add(new KeywordToken(KeywordType.IF));
                return ret;
            }
            if(type==KeywordType.BREAK)
            {
                if(breakLabel==-1)throw new GrammarException("break not in a loop");
                ret.add(new AppendCodeOperation(this,new UnconditionalGotoCode(breakLabel)));
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                ret.add(new KeywordToken(KeywordType.BREAK));
                return ret;
            }
            if(type==KeywordType.CONTINUE)
            {
                if(breakLabel==-1)throw new GrammarException("continue not in a loop");
                ret.add(new AppendCodeOperation(this,new UnconditionalGotoCode(continueLabel)));
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                ret.add(new KeywordToken(KeywordType.CONTINUE));
                return ret;
            }
            if(type==KeywordType.GOTO)
            {
                if(!(second instanceof IdentifierToken identifierToken))
                {
                    throw new GrammarException("goto label not named with an identifier");
                }
                ret.add(new AppendCodeOperation(this,new UnconditionalGotoCode(lookupLabel(identifierToken.id))));
                ret.add(new OperatorToken(OperatorType.SEMICOLON));
                ret.add(new IdentifierToken(identifierToken.id));
                ret.add(new KeywordToken(KeywordType.GOTO));
                return ret;
            }
            //TODO idk
            return null;
        }
        else if(second instanceof OperatorToken operatorToken&&operatorToken.type==OperatorType.LABEL)
        {
            if(!(next instanceof IdentifierToken identifierToken))
            {
                throw new GrammarException("label not named with an identifier");
            }
            ret.add(new AppendCodeOperation(this,new LabelCode(lookupLabel(identifierToken.id))));
            ret.add(new OperatorToken(OperatorType.LABEL));
            ret.add(new IdentifierToken(identifierToken.id));
            return ret;
        }
        else
        {
            ret.add(new OperatorToken(OperatorType.SEMICOLON));
            Expression e=new Expression();
            ret.add(new AppendCodeSeqOperation(this,e));
            ret.add(e);
            return ret;
        }
    }
}

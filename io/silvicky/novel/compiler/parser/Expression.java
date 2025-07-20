package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.code.AssignNumberCode;
import io.silvicky.novel.compiler.parser.operation.AppendCodeOperation;
import io.silvicky.novel.compiler.parser.operation.AppendCodeSeqOperation;
import io.silvicky.novel.compiler.parser.operation.AppendExpressionCodeOperation;
import io.silvicky.novel.compiler.tokens.*;

import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.compiler.Compiler.lookupVariable;
import static io.silvicky.novel.compiler.Compiler.requestInternalVariable;

public class Expression extends NonTerminal
{
    public final long resultId;
    public Expression(){this.resultId=requestInternalVariable();}
    @Override
    public List<Token> lookup(Token next, Token second) throws GrammarException
    {
        //TODO
        List<Token> ret=new ArrayList<>();
        if(next instanceof IdentifierToken)
        {
            if(second instanceof OperatorToken&&((OperatorToken) second).type()==OperatorType.EQUAL)
            {
                Expression rvalue=new Expression();
                IdentifierToken lvalue=new IdentifierToken(((IdentifierToken) next).id());
                ret.add(new AppendCodeOperation(this,new AssignCode(this.resultId,lookupVariable(lvalue.id()),0,OperatorType.NOP)));
                ret.add(new AppendCodeOperation(this,new AssignCode(lookupVariable(lvalue.id()),rvalue.resultId,0,OperatorType.NOP)));
                ret.add(new AppendCodeSeqOperation(this,rvalue));
                ret.add(rvalue);
                ret.add(new OperatorToken(OperatorType.EQUAL));
                ret.add(lvalue);
            }
            else
            {
                IdentifierToken lvalue=new IdentifierToken(((IdentifierToken) next).id());
                ret.add(new AppendCodeOperation(this,new AssignCode(this.resultId,lookupVariable(lvalue.id()),0,OperatorType.NOP)));
                ret.add(lvalue);
            }
            return ret;
        }
        if(next instanceof NumberToken)
        {
            ret.add(new AppendCodeOperation(this,new AssignNumberCode(this.resultId,((NumberToken) next).value())));
            ret.add(new NumberToken(((NumberToken) next).value()));
            return ret;
        }
        Expression lvalue=new Expression();
        Expression rvalue=new Expression();
        BinaryOperator operator=new BinaryOperator();
        ret.add(new AppendExpressionCodeOperation(this,lvalue,rvalue,operator));
        ret.add(new AppendCodeSeqOperation(this,rvalue));
        ret.add(new AppendCodeSeqOperation(this,lvalue));
        ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
        ret.add(rvalue);
        ret.add(operator);
        ret.add(lvalue);
        ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
        return ret;
    }
}

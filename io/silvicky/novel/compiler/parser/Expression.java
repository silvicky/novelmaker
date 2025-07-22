package io.silvicky.novel.compiler.parser;

import io.silvicky.novel.compiler.code.AssignCode;
import io.silvicky.novel.compiler.code.AssignNumberCode;
import io.silvicky.novel.compiler.code.AssignVariableNumberCode;
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
    public final int resultId;
    public Expression(){this.resultId=requestInternalVariable();}
    @Override
    public List<AbstractToken> lookup(AbstractToken next, AbstractToken second)
    {
        //TODO Better parsing
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof IdentifierToken identifierToken)
        {
            if(second instanceof OperatorToken operatorToken)
            {
                if(operatorToken.type.properties== OperatorType.OperatorArgsProperties.INVALID)
                {
                    IdentifierToken lvalue=new IdentifierToken(identifierToken.id);
                    ret.add(new AppendCodeOperation(this,new AssignCode(this.resultId,lookupVariable(lvalue.id),0,OperatorType.NOP)));
                    ret.add(lvalue);
                    return ret;
                }
                if(operatorToken.type.properties== OperatorType.OperatorArgsProperties.BINARY_ASSIGN)
                {
                    Expression rvalue = new Expression();
                    IdentifierToken lvalue = new IdentifierToken(identifierToken.id);
                    int lvalueId = lookupVariable(lvalue.id);
                    ret.add(new AppendCodeOperation(this, new AssignCode(this.resultId, lvalueId, 0, OperatorType.NOP)));
                    ret.add(new AppendCodeOperation(this, new AssignCode(lvalueId, lvalueId, rvalue.resultId, operatorToken.type.baseType)));
                    ret.add(new AppendCodeSeqOperation(this, rvalue));
                    ret.add(rvalue);
                    ret.add(new OperatorToken(operatorToken.type));
                    ret.add(lvalue);
                    return ret;
                }
                else if(operatorToken.type==OperatorType.QUESTION)
                {
                    //TODO Ternary
                }
                else if(operatorToken.type.properties== OperatorType.OperatorArgsProperties.BINARY)
                {
                    Expression rvalue = new Expression();
                    IdentifierToken lvalue = new IdentifierToken(identifierToken.id);
                    ret.add(new AppendCodeOperation(this, new AssignCode(this.resultId, lookupVariable(lvalue.id), rvalue.resultId, operatorToken.type)));
                    ret.add(new AppendCodeSeqOperation(this, rvalue));
                    ret.add(rvalue);
                    ret.add(new OperatorToken(operatorToken.type));
                    ret.add(lvalue);
                    return ret;
                }
                else if(operatorToken.type.properties== OperatorType.OperatorArgsProperties.UNARY)
                {
                    int lvalueId = lookupVariable(identifierToken.id);
                    ret.add(new AppendCodeOperation(this, new AssignVariableNumberCode(lvalueId,lvalueId,1,operatorToken.type.baseType)));
                    ret.add(new AppendCodeOperation(this, new AssignCode(this.resultId,lvalueId,0,OperatorType.NOP)));
                    ret.add(new OperatorToken(operatorToken.type));
                    ret.add(new IdentifierToken(identifierToken.id));
                    return ret;
                }
            }
            //TODO Consider unary
            else
            {
                IdentifierToken lvalue=new IdentifierToken(identifierToken.id);
                ret.add(new AppendCodeOperation(this,new AssignCode(this.resultId,lookupVariable(lvalue.id),0,OperatorType.NOP)));
                ret.add(lvalue);
            }
            return ret;
        }
        if(next instanceof NumberToken numberToken)
        {
            ret.add(new AppendCodeOperation(this,new AssignNumberCode(this.resultId,numberToken.value)));
            ret.add(new NumberToken(numberToken.value));
            return ret;
        }
        if(next instanceof OperatorToken operatorToken)
        {
            if(operatorToken.type==OperatorType.L_PARENTHESES)
            {
                if (second instanceof IdentifierToken || second instanceof NumberToken)
                {
                    Expression expression = new Expression();
                    ret.add(new AppendCodeOperation(this, new AssignCode(this.resultId, expression.resultId, 0, OperatorType.NOP)));
                    ret.add(new AppendCodeSeqOperation(this, expression));
                    ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                    ret.add(expression);
                    ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                    return ret;
                }
                Expression lvalue = new Expression();
                Expression rvalue = new Expression();
                BinaryOperator operator = new BinaryOperator();
                ret.add(new AppendExpressionCodeOperation(this, lvalue, rvalue, operator));
                ret.add(new AppendCodeSeqOperation(this, rvalue));
                ret.add(new AppendCodeSeqOperation(this, lvalue));
                ret.add(new OperatorToken(OperatorType.R_PARENTHESES));
                ret.add(rvalue);
                ret.add(operator);
                ret.add(lvalue);
                ret.add(new OperatorToken(OperatorType.L_PARENTHESES));
                return ret;
            }
            else if(operatorToken.type.properties== OperatorType.OperatorArgsProperties.UNARY_R)
            {
                Expression rvalue = new Expression();
                ret.add(new AppendCodeOperation(this, new AssignCode(this.resultId,rvalue.resultId,0,operatorToken.type)));
                ret.add(new AppendCodeSeqOperation(this, rvalue));
                ret.add(rvalue);
                ret.add(new OperatorToken(operatorToken.type));
                return ret;
            }
            else if(operatorToken.type.properties== OperatorType.OperatorArgsProperties.UNARY)
            {
                if (!(second instanceof IdentifierToken identifierToken))
                {
                    throw new GrammarException("not an l-value");
                }
                int lvalueId = lookupVariable(identifierToken.id);
                ret.add(new AppendCodeOperation(this, new AssignCode(this.resultId,lvalueId,0,OperatorType.NOP)));
                ret.add(new AppendCodeOperation(this, new AssignVariableNumberCode(lvalueId,lvalueId,1,operatorToken.type.baseType)));
                ret.add(new IdentifierToken(identifierToken.id));
                ret.add(new OperatorToken(operatorToken.type));
                return ret;
            }
        }
        throw new GrammarException(this.getClass().getSimpleName()+next+second);
    }
}

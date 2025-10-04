package io.silvicky.novel.util;

import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.NonTerminal;
import io.silvicky.novel.compiler.parser.expression.ConditionalExpression;
import io.silvicky.novel.compiler.parser.expression.LTRExpression;
import io.silvicky.novel.compiler.parser.operation.Operation;
import io.silvicky.novel.compiler.parser.operation.Skip;
import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.*;
import io.silvicky.novel.json.entities.*;

import java.util.List;
import java.util.Stack;

import static io.silvicky.novel.compiler.Compiler.match;
import static io.silvicky.novel.compiler.types.PrimitiveType.BOOL;
import static io.silvicky.novel.compiler.types.Type.ADDRESS_TYPE;

public class Util
{
    public static <T> void addNonNull(List<T> list, T element)
    {
        if(element==null)return;
        list.add(element);
    }

    public static <T extends LTRExpression> T rotateLeft(T root)
    {
        T currentRoot=root;
        T right;
        while(true)
        {
            try
            {
                right = (T) (currentRoot.right);
            }
            catch(ClassCastException e)
            {
                return currentRoot;
            }
            if(right==null)
            {
                if(currentRoot.left.getClass().equals(root.getClass()))return (T) currentRoot.left;
                else return currentRoot;
            }
            currentRoot.right=right.left;
            right.left=currentRoot;
            currentRoot=right;
        }
    }
    public static boolean isFunction(Type a)
    {
        if(a instanceof FunctionType)return true;
        return a instanceof PointerType pointerType && pointerType.baseType() instanceof FunctionType;
    }
    public static Type getResultType(Type a, Type b, OperatorType op)
    {
        //TODO
        if(op==OperatorType.NOP)return a;
        if(op==OperatorType.COMMA)return b;
        while(a instanceof ConstType ca)a=ca.baseType();
        while(b instanceof ConstType cb)b=cb.baseType();
        if(isFunction(a) || isFunction(b))throw new GrammarException("functions and their pointers cannot participate in operations");
        if(a instanceof AbstractPointer&&b instanceof AbstractPointer)throw new GrammarException("two pointers/arrays cannot be added");
        if(a instanceof PrimitiveType pa&&b instanceof PrimitiveType pb)return PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
        if(a instanceof PointerType)return a;
        if(b instanceof PointerType)return b;
        if(a instanceof ArrayType aa)return new PointerType(aa.baseType());
        return new PointerType(((ArrayType)b).baseType());
    }

    public static PrimitiveType getPrimitiveType(Type type)
    {
        while(type instanceof ConstType constType)type=constType.baseType();
        if(type instanceof PointerType||type instanceof ArrayType||type instanceof FunctionType)
        {
            return ADDRESS_TYPE;
        }
        if(type instanceof PrimitiveType primitiveType)return primitiveType;
        throw new GrammarException("Unknown type");
    }

    public static Object castPrimitiveType(Object source, PrimitiveType targetType, PrimitiveType sourceType)
    {
        //TODO Consider unsigned
        Number number;
        if(source instanceof Boolean bl)
        {
            if(targetType==BOOL)return source;
            number=bl?1:0;
        }
        else number=(Number) source;
        switch (targetType)
        {
            case BOOL ->
            {
                if(number instanceof Float flt)return flt!=0;
                if(number instanceof Double dbl)return dbl!=0;
                return number.longValue()!=0;
            }
            case INT, UNSIGNED_INT, LONG, UNSIGNED_LONG ->
            {
                return number.intValue();
            }
            case CHAR, UNSIGNED_CHAR ->
            {
                return number.byteValue();
            }
            case SHORT, UNSIGNED_SHORT ->
            {
                return number.shortValue();
            }
            case LONG_LONG,UNSIGNED_LONG_LONG ->
            {
                return number.longValue();
            }
            case FLOAT ->
            {
                return number.floatValue();
            }
            case DOUBLE,LONG_DOUBLE ->
            {
                return number.doubleValue();
            }
            default -> throw new RuntimeException("invalid cast");
        }
    }
    public static Pair<PrimitiveType,Object> calculateConstExpr(Pair<PrimitiveType,Object> a,Pair<PrimitiveType,Object> b, OperatorType op)
    {
        PrimitiveType commonType=PrimitiveType.values()[Math.max(a.first().ordinal(),b.first().ordinal())];
        return switch (op)
        {
            case OR_OR -> new Pair<>(BOOL,((boolean)castPrimitiveType(a.second(),BOOL,a.first()))||((boolean) castPrimitiveType(b.second(),BOOL,b.first())));
            case AND_AND -> new Pair<>(BOOL,((boolean)castPrimitiveType(a.second(),BOOL,a.first()))&&((boolean) castPrimitiveType(b.second(),BOOL,b.first())));
            default -> new Pair<>((PrimitiveType) getResultType(a.first(),b.first(),op),
                    op.operation.cal(
                            castPrimitiveType(a.second(),commonType,a.first()),
                            castPrimitiveType(b.second(),commonType,b.first()),
                            commonType));
        };
    }
    public static Pair<PrimitiveType,Object> parseConstExpr(List<AbstractToken> tokens)
    {
        ConditionalExpression conditionalExpression=new ConditionalExpression();
        tokens.add(new OperatorToken(OperatorType.SEMICOLON));
        tokens.add(new OperatorToken(OperatorType.SEMICOLON));
        int rul=0;
        Stack<AbstractToken> stack=new Stack<>();
        stack.push(conditionalExpression);
        while(!stack.empty())
        {
            AbstractToken top=stack.pop();
            if(top instanceof Operation operation)
            {
                operation.execute();
                continue;
            }
            if(top instanceof Skip)
            {
                stack.pop();
                continue;
            }
            AbstractToken next= tokens.get(rul);
            AbstractToken second= tokens.get(rul+1);
            if(!(top instanceof NonTerminal nonTerminal))
            {
                if(!match(top,next))throw new GrammarException("Mismatch: "+top+" and "+next);
                rul++;
                continue;
            }
            List<AbstractToken> list=nonTerminal.lookup(next,second);
            for(AbstractToken abstractToken :list)stack.push(abstractToken);
        }
        return conditionalExpression.evaluateConstExpr();
    }
    public static JsonEntity findJsonEntity(AbstractToken next)
    {
        if(next instanceof StringToken stringToken)return new StringEntity(stringToken.content);
        if(next instanceof IdentifierToken identifierToken)
        {
            if(identifierToken.id.equals("true"))return new BoolEntity(true);
            if(identifierToken.id.equals("false"))return new BoolEntity(false);
            if(identifierToken.id.equals("null"))return new NullEntity();
        }
        if(next instanceof OperatorToken operatorToken)
        {
            if(operatorToken.type==OperatorType.L_BRACE)return new MapEntity();
            if(operatorToken.type==OperatorType.L_BRACKET)return new ListEntity();
        }
        return new NumberEntity();
    }
}

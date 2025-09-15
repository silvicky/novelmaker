package io.silvicky.novel.util;

import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.expression.LTRExpression;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.*;

import java.util.List;

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
            default ->
            {
                return null;
            }
        }
    }
}

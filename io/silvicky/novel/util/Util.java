package io.silvicky.novel.util;

import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.parser.expression.LTRExpression;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.*;

import java.util.List;

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
}

package io.silvicky.novel.compiler.tokens;

import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.types.*;
import io.silvicky.novel.util.Pair;

public enum OperatorType
{
    //TODO everything here
    L_PARENTHESES("("),
    R_PARENTHESES(")"),
    L_BRACKET("["),
    R_BRACKET("]"),
    L_BRACE("{"),
    R_BRACE("}"),
    LESS("<",OperatorArgsProperties.BINARY,(a,b,ta,tb)->new Pair<>(PrimitiveType.BOOL,(a<b)?1L:0L)),
    GREATER(">",OperatorArgsProperties.BINARY,(a,b,ta,tb)->LESS.operation.cal(b,a,tb,ta)),
    LESS_EQUAL("<=",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
            new Pair<>(PrimitiveType.BOOL,1L-LESS.operation.cal(b,a,tb,ta).second())),
    GREATER_EQUAL(">=",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
            new Pair<>(PrimitiveType.BOOL,1L-LESS.operation.cal(a,b,ta,tb).second())),
    NOT_EQUAL("!=",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
            new Pair<>(PrimitiveType.BOOL,(LESS.operation.cal(a,b,ta,tb).second()+LESS.operation.cal(b,a,tb,ta).second())==0L?0L:1L)),
    EQUAL_EQUAL("==",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
            new Pair<>(PrimitiveType.BOOL,(LESS.operation.cal(a,b,ta,tb).second()+LESS.operation.cal(b,a,tb,ta).second())==0L?1L:0L)),
    L_SHIFT("<<",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
        if(!(pa.isInteger()&&pb.isInteger()))throw new GrammarException("not integer");
        PrimitiveType ret=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
        long ans=a<<b;
        return new Pair<>(ret,ans);
    }),
    R_SHIFT(">>",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
        if(!(pa.isInteger()&&pb.isInteger()))throw new GrammarException("not integer");
        PrimitiveType ret=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
        long ans=a>>b;
        return new Pair<>(ret,ans);
    }),
    L_SHIFT_EQUAL("<<=",OperatorArgsProperties.BINARY_ASSIGN,L_SHIFT),
    R_SHIFT_EQUAL(">>=",OperatorArgsProperties.BINARY_ASSIGN,R_SHIFT),
    NOT("!",OperatorArgsProperties.UNARY_R,(a,b,ta,tb)->new Pair<>(PrimitiveType.BOOL,(a==0)?1L:0L)),
    COLON(":"),
    QUESTION("?"),
    LABEL(":;"),
    SEMICOLON(";"),
    COMMA(",",OperatorArgsProperties.BINARY,(a,b,ta,tb)->new Pair<>(tb,b)),
    DOT("."),
    MULTIPLY("*",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
        PrimitiveType ret=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
        long ans=a*b;
        return new Pair<>(ret,ans);
    }),
    MULTIPLY_EQUAL("*=",OperatorArgsProperties.BINARY_ASSIGN,MULTIPLY),
    PLUS("+",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(ta instanceof ArrayType aa)ta=new PointerType(aa.baseType());
        if(tb instanceof ArrayType ab)tb=new PointerType(ab.baseType());
        Type type;
        long ans;
        if(ta instanceof PointerType&&tb instanceof PointerType)throw new GrammarException("addition between pointers");
        else if(tb instanceof PointerType pb)
        {
            type=tb;
            ans=b+a*pb.baseType().getSize();
        }
        else if(ta instanceof PointerType pa)
        {
            type=ta;
            ans=a+b*pa.baseType().getSize();
        }
        else
        {
            if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
            type=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
            ans=a+b;
        }
        return new Pair<>(type,ans);
    }),
    PLUS_EQUAL("+=",OperatorArgsProperties.BINARY_ASSIGN,PLUS),
    PLUS_PLUS("++",OperatorArgsProperties.UNARY,PLUS),
    MINUS("-",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(ta instanceof ArrayType aa)ta=new PointerType(aa.baseType());
        if(tb instanceof ArrayType ab)tb=new PointerType(ab.baseType());
        Type type;
        long ans;
        if(ta instanceof PointerType pa&&tb instanceof PointerType pb)
        {
            if(!pa.equals(pb))throw new GrammarException("minus between different pointers");
            type=PrimitiveType.INT;
            ans=(b-a)/pb.baseType().getSize();
        }
        else if(tb instanceof PointerType)
        {
            throw new GrammarException("number minus pointer");
        }
        else if(ta instanceof PointerType pa)
        {
            type=ta;
            ans=a-b*pa.baseType().getSize();
        }
        else
        {
            if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
            type=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
            ans=a-b;
        }
        return new Pair<>(type,ans);
    }),
    MINUS_EQUAL("-=",OperatorArgsProperties.BINARY_ASSIGN,MINUS),
    MINUS_MINUS("--",OperatorArgsProperties.UNARY,MINUS),
    DIVIDE("/",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
        PrimitiveType ret=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
        long ans=a/b;
        return new Pair<>(ret,ans);
    }),
    DIVIDE_EQUAL("/=",OperatorArgsProperties.BINARY_ASSIGN,DIVIDE),
    MOD("%",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
        if(!(pa.isInteger()&&pb.isInteger()))throw new GrammarException("not integer");
        PrimitiveType ret=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
        long ans=a%b;
        return new Pair<>(ret,ans);
    }),
    MOD_EQUAL("%=",OperatorArgsProperties.BINARY_ASSIGN,MOD),
    BACKSLASH("\\"),
    REVERSE("~",OperatorArgsProperties.UNARY_R,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        if(!(ta instanceof PrimitiveType pa))throw new GrammarException("not number");
        if(!(pa.isInteger()))throw new GrammarException("not integer");
        long ans=~a;
        return new Pair<>(pa,ans);
    }),
    OR("|",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
        if(!(pa.isInteger()&&pb.isInteger()))throw new GrammarException("not integer");
        PrimitiveType ret=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
        long ans=a|b;
        return new Pair<>(ret,ans);
    }),
    OR_EQUAL("|=",OperatorArgsProperties.BINARY_ASSIGN,OR),
    OR_OR("||",OperatorArgsProperties.BINARY,(a,b,ta,tb)->new Pair<>(PrimitiveType.BOOL,((a!=0)||(b!=0))?1L:0L)),
    AND("&",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
        if(!(pa.isInteger()&&pb.isInteger()))throw new GrammarException("not integer");
        PrimitiveType ret=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
        long ans=a&b;
        return new Pair<>(ret,ans);
    }),
    AND_EQUAL("&=",OperatorArgsProperties.BINARY_ASSIGN,AND),
    AND_AND("&&",OperatorArgsProperties.BINARY,(a,b,ta,tb)->new Pair<>(PrimitiveType.BOOL,((a!=0)&&(b!=0))?1L:0L)),
    XOR("^",OperatorArgsProperties.BINARY,(a,b,ta,tb)->
    {
        while(ta instanceof ConstType ca)ta=ca.baseType();
        while(tb instanceof ConstType cb)tb=cb.baseType();
        if(!(ta instanceof PrimitiveType pa&&tb instanceof PrimitiveType pb))throw new GrammarException("not number");
        if(!(pa.isInteger()&&pb.isInteger()))throw new GrammarException("not integer");
        PrimitiveType ret=PrimitiveType.values()[Math.max(pa.ordinal(),pb.ordinal())];
        long ans=a^b;
        return new Pair<>(ret,ans);
    }),
    XOR_EQUAL("^=",OperatorArgsProperties.BINARY_ASSIGN,XOR),
    NOP("",OperatorArgsProperties.UNARY_R,(a,b,ta,tb)->new Pair<>(ta,a)),
    EQUAL("=",OperatorArgsProperties.BINARY_ASSIGN,COMMA)
    ;
    public enum OperatorArgsProperties
    {
        UNARY_R,
        UNARY,
        BINARY,
        INVALID,
        BINARY_ASSIGN
    }
    public final String symbol;
    public final OperatorArgsProperties properties;
    public final OperatorOperation operation;
    public final OperatorType baseType;
    OperatorType(String symbol, OperatorArgsProperties properties, OperatorOperation operation, OperatorType baseType)
    {
        this.symbol=symbol;
        this.properties=properties;
        this.operation=operation;
        this.baseType=baseType;
    }
    OperatorType(String symbol, OperatorArgsProperties properties, OperatorType baseType)
    {this(symbol,properties, baseType.operation,baseType);}
    OperatorType(String symbol, OperatorArgsProperties properties, OperatorOperation operation)
    {this(symbol,properties,operation,null);}
    OperatorType(String symbol)
    {this(symbol,OperatorArgsProperties.INVALID,null,null);}
    public static OperatorType find(String s)
    {
        for(OperatorType type:OperatorType.values())
        {
            if(type.symbol.equals(s))return type;
        }
        return null;
    }
}

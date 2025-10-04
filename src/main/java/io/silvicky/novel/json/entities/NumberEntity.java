package io.silvicky.novel.json.entities;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.NumberToken;
import io.silvicky.novel.compiler.tokens.OperatorToken;
import io.silvicky.novel.compiler.tokens.OperatorType;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static io.silvicky.novel.util.Util.calculateConstExpr;
import static io.silvicky.novel.util.Util.castPrimitiveType;

public class NumberEntity implements JsonEntity
{
    public Number value;
    private boolean isNegative;
    @Override
    public List<AbstractToken> lookup(AbstractToken next)
    {
        List<AbstractToken> ret=new ArrayList<>();
        if(next instanceof NumberToken<?> numberToken)
        {
            value=(Number) numberToken.value;
            isNegative=false;
            ret.add(numberToken);
            return ret;
        }
        OperatorType operatorType=((OperatorToken)next).type;
        if(operatorType==OperatorType.MINUS)
        {
            isNegative=true;
            ret.add(new NumberEntityResidue(this));
        }
        else if(operatorType==OperatorType.PLUS)
        {
            isNegative=false;
            ret.add(new NumberEntityResidue(this));
        }
        throw new RuntimeException("unknown number");
    }

    @Override
    public Object adapt(Type type)
    {
        Pair<PrimitiveType,Object> pr=calculateConstExpr(
                new Pair<>(PrimitiveType.getPrimitiveTypeByJava(value.getClass()),value),
                new Pair<>(PrimitiveType.INT,isNegative?-1:1),
                OperatorType.MULTIPLY);
        return castPrimitiveType(pr.second(),
                PrimitiveType.getPrimitiveTypeByJava((Class<?>) type),
                pr.first());
    }
}

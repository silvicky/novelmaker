package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.IdentifierToken;
import io.silvicky.novel.compiler.tokens.InvalidTokenException;
import io.silvicky.novel.compiler.tokens.PreprocessorToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class Preprocessor
{
    public static List<AbstractToken> preprocessor(List<AbstractToken> abstractTokens)
    {
        List<AbstractToken> ret=new ArrayList<>();
        Iterator<AbstractToken> it=abstractTokens.iterator();
        Stack<Boolean> ifStack=new Stack<>();
        while(it.hasNext())
        {
            AbstractToken token=it.next();
            if(token== PreprocessorToken.EOF)
            {
                ret.add(PreprocessorToken.EOF);
                ret.add(PreprocessorToken.EOF);
                break;
            }
            if(token==PreprocessorToken.EOL)continue;
            if(token==PreprocessorToken.SHARP)
            {
                token= it.next();
                if(token instanceof IdentifierToken identifierToken)
                {
                    switch (identifierToken.id)
                    {
                        case "include"->
                        {
                            token=it.next();

                        }
                        default -> throw new InvalidTokenException("unknown instruction");
                    }
                }
                //TODO if, include, define
            }
            do
            {
                ret.add(token);
                token=it.next();
            }
            while(token!=PreprocessorToken.EOL);
        }
        return ret;
    }
}

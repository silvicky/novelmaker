package io.silvicky.novel.novel.output;

import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.util.Pair;

import java.util.*;

import static io.silvicky.novel.compiler.types.PrimitiveType.BOOL;
import static io.silvicky.novel.novel.Main.macros;
import static io.silvicky.novel.util.Util.*;

public class Preprocessor {
    public static List<AbstractToken> lexer(String input) {
        List<AbstractToken> ret = new ArrayList<>();
        TokenBuilder tokenBuilder = null;
        for (int pos = 0; pos < input.length(); pos++) {
            char c = input.charAt(pos);
            if (tokenBuilder == null) tokenBuilder = new TokenBuilder(null, 0, 0);//TODO we don't use it
            if (!tokenBuilder.append(c)) {
                addNonNull(ret, tokenBuilder.build());
                tokenBuilder = new TokenBuilder(null, 0, 0);
                if (!tokenBuilder.append(c)) tokenBuilder = null;
            }
            if (pos == input.length() - 1) {
                if (tokenBuilder != null) addNonNull(ret, tokenBuilder.build());
                tokenBuilder = null;
            }
        }
        return ret;
    }
    public static List<String> preprocessor(List<String> source)
    {
        List<String> ret=new ArrayList<>();
        Stack<Pair<Boolean, Pair<Boolean,Boolean>>> ifStack=new Stack<>();
        ifStack.push(new Pair<>(true,new Pair<>(false,false)));
        int falseCnt=0;
        for(String s:source)
        {
            if(s.length()>=2&&s.charAt(0)=='#')
            {
                List<AbstractToken> tokens=lexer(s);
                Iterator<AbstractToken> it=tokens.iterator();
                it.next();
                AbstractToken token= tokens.get(1);
                String id;
                if(token instanceof IdentifierToken identifierToken)id=identifierToken.id;
                else if(token instanceof KeywordToken keywordToken)id=keywordToken.type.symbol;
                else throw new RuntimeException("invalid instruction");
                tokens=tokens.subList(2,tokens.size());
                for(int i=0;i<tokens.size();i++)
                {
                    AbstractToken token2=tokens.get(i);
                    if(token2 instanceof IdentifierToken identifierToken){
                        tokens.set(i,new NumberToken<>(macros.contains(identifierToken.id), BOOL, null, 0, 0));
                    }
                }
                switch (id)
                {
                    case "if" ->
                    {
                        if(!ifStack.peek().first())falseCnt++;
                        if(falseCnt!=0)
                        {
                            ifStack.push(new Pair<>(false,new Pair<>(true,false)));
                            continue;
                        }
                        Pair<PrimitiveType,Object> pr=parseConstExpr(tokens);
                        boolean result=(boolean)castPrimitiveType(pr.second(),BOOL,pr.first());
                        if(result)
                        {
                            ifStack.push(new Pair<>(true,new Pair<>(true,false)));
                        }
                        else
                        {
                            ifStack.push(new Pair<>(false,new Pair<>(false,false)));
                        }
                    }
                    case "elif" ->
                    {
                        if(falseCnt!=0)
                        {
                            continue;
                        }
                        if(ifStack.peek().second().second())throw new RuntimeException("else after else");
                        if(ifStack.peek().second().first())
                        {
                            ifStack.pop();
                            ifStack.push(new Pair<>(false,new Pair<>(true,false)));
                            continue;
                        }
                        ifStack.pop();
                        Pair<PrimitiveType,Object> pr=parseConstExpr(tokens);
                        boolean result=(boolean)castPrimitiveType(pr.second(),BOOL,pr.first());
                        if(result)
                        {
                            ifStack.push(new Pair<>(true,new Pair<>(true,false)));
                        }
                        else
                        {
                            ifStack.push(new Pair<>(false,new Pair<>(false,false)));
                        }
                    }
                    case "else" ->
                    {
                        if(falseCnt!=0)
                        {
                            continue;
                        }
                        if(ifStack.peek().second().second())throw new RuntimeException("else after else");
                        if(ifStack.peek().second().first())
                        {
                            ifStack.pop();
                            ifStack.push(new Pair<>(false,new Pair<>(true,false)));
                            continue;
                        }
                        ifStack.pop();
                        if(!tokens.isEmpty())throw new RuntimeException("invalid else");
                        ifStack.push(new Pair<>(true,new Pair<>(true,false)));
                    }
                    case "endif" ->
                    {
                        ifStack.pop();
                        if(!ifStack.peek().first())falseCnt--;
                    }
                    default -> throw new InvalidTokenException("unknown instruction");
                }
            }
            else
            {
                if(ifStack.peek().first())ret.add(s);
            }
        }
        ifStack.pop();
        if(!ifStack.empty())throw new RuntimeException("if not terminated");
        return ret;
    }
}

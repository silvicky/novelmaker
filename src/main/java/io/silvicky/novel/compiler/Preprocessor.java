package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.tokens.*;
import io.silvicky.novel.compiler.types.PrimitiveType;
import io.silvicky.novel.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static io.silvicky.novel.compiler.types.PrimitiveType.BOOL;
import static io.silvicky.novel.util.Util.*;

public class Preprocessor
{
    public static boolean isPreprocessing=false;
    private static final Map<String,Rule> definitions=new HashMap<>();
    public static boolean isDefined(String s){return definitions.containsKey(s);}
    public static final Path libraryPath=Path.of(".");//TODO
    private static List<AbstractToken> lexer(Path input)
    {
        List<AbstractToken> ret=new ArrayList<>();
        TokenBuilder tokenBuilder=null;
        try(BufferedReader bufferedReader=new BufferedReader(new FileReader(input.toFile())))
        {
            String cur;
            char las = 0;
            boolean isGlobalComment = false;
            boolean isBackslashConnected = false;
            for (int line = 1; ; line++)
            {
                if (!isBackslashConnected)
                {
                    las = 0;
                }
                isBackslashConnected = false;
                cur = bufferedReader.readLine();
                if (cur == null) break;
                for (int pos = 0; pos < cur.length(); pos++)
                {
                    char c = cur.charAt(pos);
                    if (isGlobalComment)
                    {
                        if (las == '*' && c == '/')
                        {
                            isGlobalComment = false;
                            las = 0;
                        }
                        else las = c;
                        continue;
                    }
                    if (las == '/' && c == '/')
                    {
                        tokenBuilder = null;
                        ret.add(PreprocessorToken.EOL);
                        break;
                    }
                    if (las == '/' && c == '*')
                    {
                        tokenBuilder = null;
                        isGlobalComment = true;
                        continue;
                    }
                    if (c == '\\' && pos == cur.length() - 1)
                    {
                        isBackslashConnected = true;
                        continue;
                    }
                    if (tokenBuilder == null) tokenBuilder = new TokenBuilder(input.toString(), line, pos + 1);
                    if (!tokenBuilder.append(c))
                    {
                        addNonNull(ret, tokenBuilder.build());
                        tokenBuilder = new TokenBuilder(input.toString(), line, pos + 1);
                        if(!tokenBuilder.append(c))tokenBuilder=null;
                    }
                    las = c;
                    if (pos == cur.length() - 1)
                    {
                        if(tokenBuilder!=null)addNonNull(ret, tokenBuilder.build());
                        tokenBuilder = null;
                        ret.add(PreprocessorToken.EOL);
                    }
                }
            }
            if (tokenBuilder != null) addNonNull(ret, tokenBuilder.build());
            return ret;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static List<AbstractToken> tokenParser(List<AbstractToken> abstractTokens)
    {
        List<AbstractToken> ret=new ArrayList<>();
        for (AbstractToken abstractToken : abstractTokens)
        {
            if (abstractToken instanceof KeywordToken keywordToken)
            {
                switch (keywordToken.type)
                {
                    case TRUE ->
                            ret.add(new NumberToken<>(true, BOOL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case FALSE ->
                            ret.add(new NumberToken<>(false, BOOL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case AND ->
                            ret.add(new OperatorToken(OperatorType.AND_AND, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case AND_EQ ->
                            ret.add(new OperatorToken(OperatorType.AND_EQUAL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case BITAND ->
                            ret.add(new OperatorToken(OperatorType.AND, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case BITOR ->
                            ret.add(new OperatorToken(OperatorType.OR, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case COMPL ->
                            ret.add(new OperatorToken(OperatorType.REVERSE, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case NOT ->
                            ret.add(new OperatorToken(OperatorType.NOT, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case NOT_EQ ->
                            ret.add(new OperatorToken(OperatorType.NOT_EQUAL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case OR ->
                            ret.add(new OperatorToken(OperatorType.OR_OR, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case OR_EQ ->
                            ret.add(new OperatorToken(OperatorType.OR_EQUAL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case XOR ->
                            ret.add(new OperatorToken(OperatorType.XOR, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    case XOR_EQ ->
                            ret.add(new OperatorToken(OperatorType.XOR_EQUAL, keywordToken.fileName, keywordToken.line, keywordToken.pos));
                    default -> ret.add(keywordToken);
                }
            }
            else if (abstractToken instanceof OperatorToken operatorToken)
            {
                switch (operatorToken.type)
                {
                    case ALT_L_BRACKET ->
                            ret.add(new OperatorToken(OperatorType.L_BRACKET, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                    case ALT_R_BRACKET ->
                            ret.add(new OperatorToken(OperatorType.R_BRACKET, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                    case ALT_L_BRACE ->
                            ret.add(new OperatorToken(OperatorType.L_BRACE, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                    case ALT_R_BRACE ->
                            ret.add(new OperatorToken(OperatorType.R_BRACE, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                    case SHARP, ALT_SHARP -> ret.add(PreprocessorToken.SHARP);
                    case SHARP_SHARP, ALT_SHARP_SHARP -> ret.add(PreprocessorToken.SHARP_SHARP);
                    case ALT_SHARP_MOD ->
                    {
                        ret.add(PreprocessorToken.SHARP);
                        ret.add(new OperatorToken(OperatorType.MOD, operatorToken.fileName, operatorToken.line, operatorToken.pos+2));
                    }
                    case DOT_DOT ->
                    {
                        ret.add(new OperatorToken(OperatorType.DOT, operatorToken.fileName, operatorToken.line, operatorToken.pos));
                        ret.add(new OperatorToken(OperatorType.DOT, operatorToken.fileName, operatorToken.line, operatorToken.pos+1));
                    }
                    default -> ret.add(abstractToken);
                }
            }
            else ret.add(abstractToken);
        }
        return ret;
    }

    private interface Rule{}
    private static class SimpleRule implements Rule
    {
        public final List<AbstractToken> result=new ArrayList<>();
    }
    private static class FunctionRule implements Rule
    {
        public final List<String> parameters =new ArrayList<>();
        public final List<AbstractToken> result=new ArrayList<>();
    }
    private static List<AbstractToken> parseDefine(List<AbstractToken> abstractTokens, Set<String> used)
    {
        List<AbstractToken> ret=new ArrayList<>();
        Stack<Pair<AbstractToken,Set<String>>> stack=new Stack<>();
        for(AbstractToken i:abstractTokens.reversed())stack.push(new Pair<>(i,used));
        while(!stack.empty())
        {
            Pair<AbstractToken,Set<String>> pr=stack.peek();
            stack.pop();
            if((!(pr.first() instanceof IdentifierToken identifierToken))||(!definitions.containsKey(identifierToken.id))||pr.second().contains(identifierToken.id))
            {
                ret.add(pr.first());
                continue;
            }
            Rule rule=definitions.get(identifierToken.id);
            //TODO ##
            List<AbstractToken> result;
            Set<String> set=new HashSet<>(pr.second());
            if(rule instanceof SimpleRule simpleRule)
            {
                result=simpleRule.result;
            }
            else
            {
                FunctionRule functionRule=(FunctionRule) rule;
                if((!(stack.pop().first() instanceof OperatorToken operatorToken))||operatorToken.type!=OperatorType.L_PARENTHESES)throw new RuntimeException("function not called");
                int lvl=1;
                List<List<AbstractToken>> args=new ArrayList<>();
                List<List<AbstractToken>> rawArgs=new ArrayList<>();
                List<AbstractToken> tmp=new ArrayList<>();
                AbstractToken cur=stack.pop().first();
                while(true)
                {
                    if(cur instanceof OperatorToken operatorToken1&&operatorToken1.type==OperatorType.L_PARENTHESES)lvl++;
                    if(cur instanceof OperatorToken operatorToken1&&operatorToken1.type==OperatorType.R_PARENTHESES)lvl--;
                    if(lvl==0)
                    {
                        args.add(parseDefine(tmp,set));
                        rawArgs.add(tmp);
                        break;
                    }
                    if(cur instanceof OperatorToken operatorToken1&&operatorToken1.type==OperatorType.COMMA&&lvl==1)
                    {
                        args.add(parseDefine(tmp,set));
                        rawArgs.add(tmp);
                        tmp=new ArrayList<>();
                    }
                    else
                    {
                        tmp.add(cur);
                    }
                    cur=stack.pop().first();
                }
                boolean isVariadic=(!functionRule.parameters.isEmpty())&&functionRule.parameters.getLast().equals("...");
                int paramsSize=functionRule.parameters.size();
                int argsSize=args.size();
                if(isVariadic)paramsSize--;
                if(isVariadic?(argsSize<paramsSize):(argsSize!=paramsSize))throw new RuntimeException("args mismatch");
                Map<String,List<AbstractToken>> replacementMap=new HashMap<>();
                for(int i=0;i<paramsSize;i++)replacementMap.put(functionRule.parameters.get(i),args.get(i));
                if(isVariadic)
                {
                    List<AbstractToken> tmpList=new ArrayList<>();
                    for(int i=paramsSize;i<argsSize;i++)
                    {
                        if(i>paramsSize)tmpList.add(new OperatorToken(OperatorType.COMMA));
                        tmpList.addAll(rawArgs.get(i));
                    }
                    replacementMap.put("__VA_ARGS__",tmpList);
                }
                Iterator<AbstractToken> it=functionRule.result.iterator();
                result=new ArrayList<>();
                while(it.hasNext())
                {
                    AbstractToken token=it.next();
                    if(token==PreprocessorToken.SHARP)
                    {
                        token=it.next();
                        String id=((IdentifierToken)token).id;
                        result.add(new StringToken(asString(replacementMap.get(id))));
                        continue;
                    }
                    if(token instanceof IdentifierToken identifierToken1&&replacementMap.containsKey(identifierToken1.id))
                    {
                        result.addAll(replacementMap.get(identifierToken1.id));
                        continue;
                    }
                    if(token instanceof IdentifierToken identifierToken1&&identifierToken1.id.equals("__VA_OPT__"))
                    {
                        if(!isVariadic)throw new RuntimeException("not variadic");
                        List<AbstractToken> optional=new ArrayList<>();
                        token=it.next();
                        if((!(token instanceof OperatorToken operatorToken1))||operatorToken1.type!=OperatorType.L_PARENTHESES)throw new RuntimeException("invalid __VA_OPT__");
                        int lvl2=1;
                        token=it.next();
                        while(true)
                        {
                            if(token instanceof OperatorToken operatorToken2&&operatorToken2.type==OperatorType.L_PARENTHESES)lvl2++;
                            if(token instanceof OperatorToken operatorToken2&&operatorToken2.type==OperatorType.R_PARENTHESES)lvl2--;
                            if(lvl2==0)break;
                            optional.add(token);
                            token=it.next();
                        }
                        if(argsSize>paramsSize)result.addAll(optional);
                        continue;
                    }
                    result.add(token);
                }
            }
            set.add(identifierToken.id);
            for(AbstractToken i:result.reversed())stack.push(new Pair<>(i,set));
        }
        return ret;
    }
    private static String asString(List<AbstractToken> tokens)
    {
        StringBuilder stringBuilder=new StringBuilder();
        for(AbstractToken token:tokens)stringBuilder.append(asString(token));
        return stringBuilder.toString();
    }
    private static String asString(AbstractToken token)
    {
        return switch (token)
        {
            case StringToken stringToken->"\""+stringToken.content+"\"";
            case IdentifierToken identifierToken->identifierToken.id;
            case OperatorToken operatorToken->operatorToken.type.symbol;
            case NumberToken<?> numberToken->numberToken.value.toString();
            case KeywordToken keywordToken->keywordToken.type.symbol;
            case PreprocessorToken preprocessorToken->preprocessorToken.symbol;
            default -> throw new InvalidTokenException("unknown token");
        };
    }
    public static List<AbstractToken> preprocessor(Path sourceFile)
    {
        List<AbstractToken> abstractTokens=tokenParser(lexer(sourceFile));
        List<AbstractToken> ret=new ArrayList<>();
        Iterator<AbstractToken> it=abstractTokens.iterator();
        Stack<Pair<Boolean,Pair<Boolean,Boolean>>> ifStack=new Stack<>();
        ifStack.push(new Pair<>(true,new Pair<>(false,false)));
        int falseCnt=0;
        List<AbstractToken> cur=new ArrayList<>();
        while(it.hasNext())
        {
            AbstractToken token=it.next();
            if(token==PreprocessorToken.SHARP)
            {
                if(ifStack.peek().first())ret.addAll(parseDefine(cur,new HashSet<>()));
                cur.clear();
                token= it.next();
                String id;
                if(token instanceof IdentifierToken identifierToken)id=identifierToken.id;
                else if(token instanceof KeywordToken keywordToken)id=keywordToken.type.symbol;
                else throw new RuntimeException("invalid instruction");
                switch (id)
                {
                    case "include" ->
                    {
                        token = it.next();
                        List<AbstractToken> headerTokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            headerTokens.add(token);
                            token = it.next();
                        }
                        if(!ifStack.peek().first())continue;
                        headerTokens = parseDefine(headerTokens, new HashSet<>());
                        if (headerTokens.size() == 1 && headerTokens.getFirst() instanceof StringToken stringToken)
                        {
                            Path header = sourceFile.getParent().resolve(stringToken.content);
                            if (header.toFile().exists() && header.toFile().isFile())
                            {
                                ret.addAll(preprocessor(header));
                                continue;
                            }
                            header = libraryPath.resolve(stringToken.content);
                            if (header.toFile().exists() && header.toFile().isFile())
                            {
                                ret.addAll(preprocessor(header));
                                continue;
                            }
                            throw new RuntimeException("file not found");
                        }
                        if (headerTokens.size() >= 2 && headerTokens.getFirst() instanceof OperatorToken o1 && o1.type == OperatorType.LESS && headerTokens.getLast() instanceof OperatorToken o2 && o2.type == OperatorType.GREATER)
                        {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 1; i < headerTokens.size() - 1; i++)
                                stringBuilder.append(asString(headerTokens.get(i)));
                            Path header = libraryPath.resolve(stringBuilder.toString());
                            if (header.toFile().exists() && header.toFile().isFile())
                            {
                                ret.addAll(preprocessor(header));
                                continue;
                            }
                            throw new RuntimeException("file not found");
                        }
                        throw new RuntimeException("file not found");
                    }
                    case "undef" ->
                    {
                        token = it.next();
                        List<AbstractToken> tokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            tokens.add(token);
                            token = it.next();
                        }
                        if(!ifStack.peek().first())continue;
                        if (tokens.size() != 1 || !(tokens.getFirst() instanceof IdentifierToken identifierToken1))
                            throw new RuntimeException("invalid undef");
                        definitions.remove(identifierToken1.id);
                    }
                    case "define" ->
                    {
                        if(!ifStack.peek().first())
                        {
                            token = it.next();
                            while (token != PreprocessorToken.EOL)
                            {
                                token = it.next();
                            }
                            continue;
                        }
                        token = it.next();
                        if (!(token instanceof IdentifierToken identifierToken1))
                            throw new RuntimeException("invalid define");
                        token = it.next();
                        List<AbstractToken> tokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            tokens.add(token);
                            token = it.next();
                        }
                        if (tokens.getFirst() instanceof OperatorToken operatorToken
                                && operatorToken.type == OperatorType.L_PARENTHESES
                                && operatorToken.line == identifierToken1.line
                                && operatorToken.pos == identifierToken1.pos + identifierToken1.id.length())
                        {
                            FunctionRule functionRule = new FunctionRule();
                            Iterator<AbstractToken> it2 = tokens.iterator();
                            AbstractToken token1, token2;
                            it2.next();
                            while (true)
                            {
                                token1 = it2.next();
                                token2 = it2.next();
                                if (token1 instanceof OperatorToken operatorToken1 && operatorToken1.type == OperatorType.ELLIPSIS)
                                {
                                    functionRule.parameters.add(OperatorType.ELLIPSIS.symbol);
                                    if ((!(token2 instanceof OperatorToken operatorToken2)) || operatorToken2.type != OperatorType.R_PARENTHESES)
                                        throw new RuntimeException("invalid define");
                                    break;
                                }
                                if (!(token1 instanceof IdentifierToken identifierToken2))
                                    throw new RuntimeException("invalid define");
                                functionRule.parameters.add(identifierToken2.id);
                                if (token2 instanceof OperatorToken operatorToken1 && operatorToken1.type == OperatorType.R_PARENTHESES)
                                    break;
                            }
                            while (it2.hasNext()) functionRule.result.add(it2.next());
                            definitions.put(identifierToken1.id, functionRule);
                        }
                        else
                        {
                            SimpleRule rule = new SimpleRule();
                            rule.result.addAll(tokens);
                            definitions.put(identifierToken1.id, rule);
                        }
                    }
                    case "if" ->
                    {
                        token = it.next();
                        List<AbstractToken> tokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            tokens.add(token);
                            token = it.next();
                        }
                        if(!ifStack.peek().first())falseCnt++;
                        if(falseCnt!=0)
                        {
                            ifStack.push(new Pair<>(false,new Pair<>(true,false)));
                            continue;
                        }
                        Pair<PrimitiveType,Object> pr=parseConstExpr(parseDefine(tokens,new HashSet<>()));
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
                    case "ifdef" ->
                    {
                        token = it.next();
                        List<AbstractToken> tokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            tokens.add(token);
                            token = it.next();
                        }
                        if(!ifStack.peek().first())falseCnt++;
                        if(falseCnt!=0)
                        {
                            ifStack.push(new Pair<>(false,new Pair<>(true,false)));
                            continue;
                        }
                        if(tokens.size()!=1||!(tokens.getFirst() instanceof IdentifierToken identifierToken))throw new RuntimeException("invalid ifdef");
                        boolean result= definitions.containsKey(identifierToken.id);
                        if(result)
                        {
                            ifStack.push(new Pair<>(true,new Pair<>(true,false)));
                        }
                        else
                        {
                            ifStack.push(new Pair<>(false,new Pair<>(false,false)));
                        }
                    }
                    case "ifndef" ->
                    {
                        token = it.next();
                        List<AbstractToken> tokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            tokens.add(token);
                            token = it.next();
                        }
                        if(!ifStack.peek().first())falseCnt++;
                        if(falseCnt!=0)
                        {
                            ifStack.push(new Pair<>(false,new Pair<>(true,false)));
                            continue;
                        }
                        if(tokens.size()!=1||!(tokens.getFirst() instanceof IdentifierToken identifierToken))throw new RuntimeException("invalid ifndef");
                        boolean result= !definitions.containsKey(identifierToken.id);
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
                        token = it.next();
                        List<AbstractToken> tokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            tokens.add(token);
                            token = it.next();
                        }
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
                        Pair<PrimitiveType,Object> pr=parseConstExpr(parseDefine(tokens,new HashSet<>()));
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
                    case "elifdef" ->
                    {
                        token = it.next();
                        List<AbstractToken> tokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            tokens.add(token);
                            token = it.next();
                        }
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
                        if(tokens.size()!=1||!(tokens.getFirst() instanceof IdentifierToken identifierToken))throw new RuntimeException("invalid elifdef");
                        boolean result= definitions.containsKey(identifierToken.id);
                        if(result)
                        {
                            ifStack.push(new Pair<>(true,new Pair<>(true,false)));
                        }
                        else
                        {
                            ifStack.push(new Pair<>(false,new Pair<>(false,false)));
                        }
                    }
                    case "elifndef" ->
                    {
                        token = it.next();
                        List<AbstractToken> tokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            tokens.add(token);
                            token = it.next();
                        }
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
                        if(tokens.size()!=1||!(tokens.getFirst() instanceof IdentifierToken identifierToken))throw new RuntimeException("invalid elifndef");
                        boolean result= !definitions.containsKey(identifierToken.id);
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
                        token = it.next();
                        List<AbstractToken> tokens = new ArrayList<>();
                        while (token != PreprocessorToken.EOL)
                        {
                            tokens.add(token);
                            token = it.next();
                        }
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
                        it.next();
                        ifStack.pop();
                        if(!ifStack.peek().first())falseCnt--;
                    }
                    default -> throw new InvalidTokenException("unknown instruction");
                }
            }
            else
            {
                while (token != PreprocessorToken.EOL)
                {
                    cur.add(token);
                    token = it.next();
                }
            }
        }
        ret.addAll(parseDefine(cur,new HashSet<>()));
        ifStack.pop();
        if(!ifStack.empty())throw new RuntimeException("if not terminated");
        return ret;
    }
}

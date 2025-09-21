package io.silvicky.novel.compiler;

import io.silvicky.novel.compiler.tokens.*;

import java.nio.file.Path;
import java.util.*;

public class Preprocessor
{
    private interface Rule{}
    private static class SimpleRule implements Rule
    {
        public final List<AbstractToken> result=new ArrayList<>();
    }
    private static class FunctionRule implements Rule
    {
        public final List<String> arguments=new ArrayList<>();
        public final List<AbstractToken> result=new ArrayList<>();
    }
    private static final Map<String,Rule> definitions=new HashMap<>();
    public static final Path libraryPath=Path.of(".");//TODO
    private static List<AbstractToken> parseDefine(List<AbstractToken> abstractTokens, Set<String> used)
    {
        //TODO
        return abstractTokens;
    }
    private static String asString(AbstractToken token)
    {
        return switch (token)
        {
            case StringToken stringToken->stringToken.content;
            case IdentifierToken identifierToken->identifierToken.id;
            case OperatorToken operatorToken->operatorToken.type.symbol;
            case NumberToken<?> numberToken->numberToken.value.toString();
            case KeywordToken keywordToken->keywordToken.type.symbol;
            default -> throw new InvalidTokenException("unknown token");
        };
    }
    public static List<AbstractToken> preprocessor(List<AbstractToken> abstractTokens, Path sourceFile)
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
                            List<AbstractToken> headerTokens=new ArrayList<>();
                            while(token!=PreprocessorToken.EOL)
                            {
                                headerTokens.add(token);
                                token=it.next();
                            }
                            headerTokens=parseDefine(headerTokens,new HashSet<>());
                            if(headerTokens.size()==1&&headerTokens.getFirst() instanceof StringToken stringToken)
                            {
                                Path header=sourceFile.getParent().resolve(stringToken.content);
                                if(header.toFile().exists()&&header.toFile().isFile())
                                {
                                    ret.addAll(preprocessor(Compiler.tokenParser(Compiler.lexer(header)),header));
                                    continue;
                                }
                                header=libraryPath.resolve(stringToken.content);
                                if(header.toFile().exists()&&header.toFile().isFile())
                                {
                                    ret.addAll(preprocessor(Compiler.tokenParser(Compiler.lexer(header)),header));
                                    continue;
                                }
                                throw new RuntimeException("file not found");
                            }
                            if(headerTokens.size()>=2&&headerTokens.getFirst() instanceof OperatorToken o1&&o1.type==OperatorType.LESS&&headerTokens.getLast() instanceof OperatorToken o2&&o2.type==OperatorType.GREATER)
                            {
                                StringBuilder stringBuilder=new StringBuilder();
                                for(int i=1;i<headerTokens.size()-1;i++)stringBuilder.append(asString(headerTokens.get(i)));
                                Path header=libraryPath.resolve(stringBuilder.toString());
                                if(header.toFile().exists()&&header.toFile().isFile())
                                {
                                    ret.addAll(preprocessor(Compiler.tokenParser(Compiler.lexer(header)),header));
                                    continue;
                                }
                                throw new RuntimeException("file not found");
                            }
                            throw new RuntimeException("file not found");
                        }
                        default -> throw new InvalidTokenException("unknown instruction");
                    }
                }
                //TODO if, define
            }
            while(token!=PreprocessorToken.EOL)
            {
                ret.add(token);
                token=it.next();
            }
        }
        return ret;
    }
}

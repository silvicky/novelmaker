package io.silvicky.novel.json;

import io.silvicky.novel.compiler.parser.GrammarException;
import io.silvicky.novel.compiler.tokens.AbstractToken;
import io.silvicky.novel.compiler.tokens.PreprocessorToken;
import io.silvicky.novel.json.entities.AbstractJsonEntity;
import io.silvicky.novel.json.entities.JsonEntity;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static io.silvicky.novel.compiler.Compiler.match;
import static io.silvicky.novel.compiler.Preprocessor.lexer;
import static io.silvicky.novel.util.Util.findJsonEntity;

public class JsonParser
{
    public static Object parseJson(Path path,Class<?> clazz)
    {
        List<AbstractToken> tokens=lexer(path);
        List<AbstractToken> tokens1=new ArrayList<>();
        for(AbstractToken token:tokens)if(token!= PreprocessorToken.EOL)tokens1.add(token);
        JsonEntity root=findJsonEntity(tokens1.getFirst());
        int rul=0;
        Stack<AbstractToken> stack=new Stack<>();
        stack.push(root);
        while(!stack.empty())
        {
            AbstractToken top=stack.pop();
            AbstractToken next= tokens1.get(rul);
            if(!(top instanceof AbstractJsonEntity abstractJsonEntity))
            {
                if(!match(top,next))throw new GrammarException("Mismatch: "+top+" and "+next);
                rul++;
                continue;
            }
            List<AbstractToken> list=abstractJsonEntity.lookup(next);
            for(AbstractToken abstractToken :list)stack.push(abstractToken);
        }
        return root.adapt(clazz);
    }
}

package io.silvicky.novel.novel;

import io.silvicky.novel.json.JsonParser;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
class Cfg
{
    boolean replaceChars=true;
    List<CharItem> chars;
    String left;
    String right;
}
public class CfgLoader
{
    public static boolean replaceChars=true;
    public static final Map<String,CharItem> charMap=new HashMap<>();
    public static String left="\\(",right="\\)";
    public static void load(Path path)
    {
        if(!path.toFile().isFile())return;
        Cfg cfg= (Cfg)JsonParser.parseJson(path,Cfg.class);
        replaceChars=cfg.replaceChars;
        if(cfg.chars!=null)for(CharItem i:cfg.chars)charMap.put(i.id,i);
        if(cfg.left!=null)left=cfg.left;
        if(cfg.right!=null)right=cfg.right;
    }
}

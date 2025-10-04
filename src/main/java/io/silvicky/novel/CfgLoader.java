package io.silvicky.novel;

import io.silvicky.novel.json.JsonParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
class CharItem
{
    String id;
    String name;
}
class Cfg
{
    boolean replaceChars=true;
    ArrayList<CharItem> chars;
    String left;
    String right;
}
public class CfgLoader
{
    public static boolean replaceChars=true;
    public static final Map<String,String> charMap=new HashMap<>();
    public static String left="\\(",right="\\)";
    public static void load(Path path)
    {
        if(!path.toFile().isFile())return;
        Cfg cfg= (Cfg)JsonParser.parseJson(path,Cfg.class);
        replaceChars=cfg.replaceChars;
        if(cfg.chars!=null)for(CharItem i:cfg.chars)charMap.put(i.id,i.name);
        if(cfg.left!=null)left=cfg.left;
        if(cfg.right!=null)right=cfg.right;
    }
}

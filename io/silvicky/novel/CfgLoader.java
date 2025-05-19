package io.silvicky.novel;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
class CharItem
{
    int id;
    String name;
}
class Cfg
{
    boolean replaceChars=true;
    List<CharItem> chars;
    String left;
    String right;
}
public class CfgLoader
{
    private static final Gson gson=new Gson();
    public static boolean replaceChars=true;
    public static final Map<Integer,String> charMap=new HashMap<>();
    public static String left="\\(",right="\\)";
    public static void load(Path path) throws FileNotFoundException
    {
        Cfg cfg=gson.fromJson(new FileReader(path.toFile()),Cfg.class);
        replaceChars=cfg.replaceChars;
        if(cfg.chars!=null)for(CharItem i:cfg.chars)charMap.put(i.id,i.name);
        if(cfg.left!=null)left=cfg.left;
        if(cfg.right!=null)right=cfg.right;
    }
}

package io.silvicky.novel;

import io.silvicky.novel.json.JsonParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class RawOrder
{
    List<String> before=new ArrayList<>();
    List<String> after=new ArrayList<>();
    List<String> ignore=new ArrayList<>();
    List<String> optional=new ArrayList<>();
    boolean isReversed=false;
}
public class Order
{
    public final List<Path> before=new ArrayList<>();
    public final List<Path> after=new ArrayList<>();
    public final HashSet<Path> ignore=new HashSet<>();
    public final HashSet<Path> optional=new HashSet<>();
    public boolean isReversed;
    public Order(Path root)
    {
        try
        {
            RawOrder rawOrder = (RawOrder) JsonParser.parseJson(root.resolve("order.json"), RawOrder.class);
            for (String i : rawOrder.before) before.add(root.resolve(i).toAbsolutePath());
            for (String i : rawOrder.after) after.add(root.resolve(i).toAbsolutePath());
            for (String i : rawOrder.ignore) ignore.add(root.resolve(i).toAbsolutePath());
            for (String i : rawOrder.optional) optional.add(root.resolve(i).toAbsolutePath());
            isReversed=rawOrder.isReversed;
        }
        catch (Exception ignored){}
    }
}
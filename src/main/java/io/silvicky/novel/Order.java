package io.silvicky.novel;

import io.silvicky.novel.json.JsonParser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

class RawOrder
{
    ArrayList<String> before=new ArrayList<>();
    ArrayList<String> after=new ArrayList<>();
    ArrayList<String> ignore=new ArrayList<>();
    ArrayList<String> optional=new ArrayList<>();
}
public class Order
{
    public final List<Path> before=new ArrayList<>();
    public final List<Path> after=new ArrayList<>();
    public final HashSet<Path> ignore=new HashSet<>();
    public final HashSet<Path> optional=new HashSet<>();
    public Order(Path root)
    {
        try
        {
            RawOrder rawOrder = (RawOrder) JsonParser.parseJson(root.resolve("order.json"), RawOrder.class);
            for (String i : rawOrder.before) before.add(root.resolve(i).toAbsolutePath());
            for (String i : rawOrder.after) after.add(root.resolve(i).toAbsolutePath());
            for (String i : rawOrder.ignore) ignore.add(root.resolve(i).toAbsolutePath());
            for (String i : rawOrder.optional) optional.add(root.resolve(i).toAbsolutePath());
        }
        catch (Exception ignored){}
    }
}
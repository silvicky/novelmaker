package io.silvicky.novel;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class RawOrder
{
    List<String> before=new ArrayList<>();
    List<String> after=new ArrayList<>();
    List<String> ignore=new ArrayList<>();

    List<String> optional=new ArrayList<>();
}
public class Order
{
    public List<Path> before=new ArrayList<>();
    public List<Path> after=new ArrayList<>();
    public Set<Path> ignore=new HashSet<>();
    public Set<Path> optional=new HashSet<>();
    public Order(Path root) throws FileNotFoundException
    {
        Gson gson=new Gson();
        File orderFile=root.resolve("order.json").toFile();
        if(!orderFile.exists())return;
        if(!orderFile.isFile())return;
        RawOrder rawOrder=gson.fromJson(new FileReader(orderFile), RawOrder.class);
        for(String i: rawOrder.before)before.add(root.resolve(i).toAbsolutePath());
        for(String i: rawOrder.after)after.add(root.resolve(i).toAbsolutePath());
        for(String i: rawOrder.ignore)ignore.add(root.resolve(i).toAbsolutePath());
        for(String i: rawOrder.optional)optional.add(root.resolve(i).toAbsolutePath());
    }
}
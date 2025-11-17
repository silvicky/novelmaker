package io.silvicky.novel.novel.output;

import io.silvicky.novel.novel.CfgLoader;
import io.silvicky.novel.novel.CharItem;
import io.silvicky.novel.novel.Main;
import io.silvicky.novel.novel.Order;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlainText
{
    private static void parseString(String line, Writer writer) throws IOException
    {
        String cur=line;
        if(CfgLoader.replaceChars)
        {
            for(Map.Entry<String, CharItem> entry:CfgLoader.charMap.entrySet())
            {
                String placeholder=CfgLoader.left+entry.getKey()+CfgLoader.right;
                cur=cur.replaceAll(placeholder, entry.getValue().name);
            }
        }
        writer.write(cur);
        writer.append('\n');
    }

    private static void parseFile(Path file, Writer writer) throws IOException
    {
        if(!file.toString().endsWith(".txt"))return;
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file.toFile()));
        String cur;
        while(true)
        {
            cur= bufferedReader.readLine();
            if(cur==null)break;
            parseString(cur,writer);
            if(Main.title)return;
        }
        writer.append('\n');
    }

    private static void parseFolder(Path inputPath, Writer writer) throws IOException
    {
        List<Path> paths=new ArrayList<>();
        Order order=new Order(inputPath);
        for(File i: Objects.requireNonNull(inputPath.toFile().listFiles()))
        {
            Path path=i.toPath().toAbsolutePath();
            if(Main.globalIgnore.contains(path)||order.before.contains(path)||order.after.contains(path)||order.ignore.contains(path))continue;
            if(path.toFile().exists())paths.add(path);
        }
        paths.sort((o1, o2) ->
        {
            if(o1.toFile().isFile()&&o2.toFile().isDirectory())return -1;
            if(o1.toFile().isDirectory()&&o2.toFile().isFile())return 1;
            if(order.isReversed)return o2.getFileName().compareTo(o1.getFileName());
            return o1.getFileName().compareTo(o2.getFileName());
        });
        for(Path i: order.before)if(Main.optional||!order.optional.contains(i))parseGeneral(i,writer);
        for(Path i: paths)if(Main.optional||!order.optional.contains(i))parseGeneral(i,writer);
        for(Path i: order.after)if(Main.optional||!order.optional.contains(i))parseGeneral(i,writer);
        writer.append('\n');
    }

    private static void parseGeneral(Path path, Writer writer) throws IOException
    {
        if(!path.toFile().exists())return;
        if (path.toFile().isFile())
        {
            parseFile(path, writer);
        }
        else
        {
            parseFolder(path, writer);
        }
    }
    public static void parseRoot(Path inputPath, Writer writer) throws IOException
    {
        parseFolder(inputPath,writer);
    }
}

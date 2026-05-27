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

import static java.lang.String.format;

public class Substitute
{
    private record FileEntity(String title,List<String> content,Path path,int depth){}
    private static final List<FileEntity> files=new ArrayList<>();
    private static String parseString(String line)
    {
        String cur=line;
        if(CfgLoader.replaceChars)
        {
            for(Map.Entry<String, CharItem> entry:CfgLoader.charMap.entrySet())
            {
                String placeholder=CfgLoader.left+entry.getKey()+CfgLoader.right;
                String coloredName=entry.getValue().name;
                cur=cur.replaceAll(placeholder, coloredName);
            }
        }
        return cur;
    }

    private static void parseFile(Path inputPath, Path outputPath,int depth) throws IOException
    {
        if(!inputPath.toString().endsWith(".txt"))return;
        BufferedReader bufferedReader=new BufferedReader(new FileReader(inputPath.toFile()));
        String cur,title=null;
        List<String> content=new ArrayList<>();
        while(true)
        {
            cur= bufferedReader.readLine();
            if(cur==null)break;
            cur=parseString(cur);
            if(title==null)title=cur;
            else content.add(cur);
        }
        String fileName=outputPath.getFileName().toString();
        files.add(new FileEntity(title,content,outputPath.getParent().resolve(fileName),depth));
    }

    private static void parseFolder(Path inputPath, Path outputPath,int depth) throws IOException
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
        List<Path> validPaths=new ArrayList<>();
        for(Path i: order.before)if(Main.optional||!order.optional.contains(i))validPaths.add(i);
        for(Path i: paths)if(Main.optional||!order.optional.contains(i))validPaths.add(i);
        for(Path i: order.after)if(Main.optional||!order.optional.contains(i))validPaths.add(i);
        boolean hasInfo=inputPath.resolve("info.txt").toFile().exists();
        for(Path i:validPaths)
        {
            parseGeneral(i,outputPath.resolve(inputPath.relativize(i)),depth);
            if(hasInfo)
            {
                depth++;
                hasInfo=false;
            }
        }
    }

    private static void parseGeneral(Path inputPath, Path outputPath,int depth) throws IOException
    {
        if(!inputPath.toFile().exists())return;
        if (inputPath.toFile().isFile())
        {
            parseFile(inputPath, outputPath,depth);
        }
        else
        {
            parseFolder(inputPath, outputPath,depth);
        }
    }
    private static void generateChapters()
    {
        for (FileEntity fileEntity : files)
        {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : fileEntity.content)
            {
                stringBuilder.append(s);
                stringBuilder.append('\n');
            }
            fileEntity.path.getParent().toFile().mkdirs();
            try (FileWriter writer = new FileWriter(fileEntity.path.toFile()))
            {
                writer.write(format("%s\n%s", fileEntity.title, stringBuilder));
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    public static void parseRoot(Path inputPath, Path outputPath) throws IOException
    {
        parseFolder(inputPath,outputPath.resolve("content"),0);
        generateChapters();
    }
}

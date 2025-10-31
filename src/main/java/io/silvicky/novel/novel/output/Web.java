package io.silvicky.novel.novel.output;

import io.silvicky.novel.novel.CfgLoader;
import io.silvicky.novel.novel.Main;
import io.silvicky.novel.novel.Order;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

public class Web
{
    private record FileEntity(String title,List<String> content,Path path,int depth){}
    private static final List<FileEntity> files=new ArrayList<>();
    private static final String htmlFormat= """
            <!DOCTYPE html>
            <html>
                <head>
                    <title>%s</title>
                </head>
                <body>
            %s
                </body>
            </html>""";
    private static final String linkFormat="<a href=\"%s\">%s</a>";
    private static String parseString(String line)
    {
        String cur=line;
        if(CfgLoader.replaceChars)
        {
            for(Map.Entry<String,String> entry:CfgLoader.charMap.entrySet())
            {
                String placeholder=CfgLoader.left+entry.getKey()+CfgLoader.right;
                cur=cur.replaceAll(placeholder, entry.getValue());
            }
        }
        return cur;
    }

    private static void parseFile(Path inputPath, Path outputPath,boolean isFirst) throws IOException
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
        int depth=outputPath.getNameCount();
        if(isFirst)depth--;
        fileName=fileName.substring(0,fileName.length()-4)+".html";
        files.add(new FileEntity(title,content,outputPath.getParent().resolve(fileName),depth));
    }

    private static void parseFolder(Path inputPath, Path outputPath) throws IOException
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
        boolean isFirst=true;
        for(Path i: order.before)if(Main.optional||!order.optional.contains(i))
        {
            parseGeneral(i,outputPath.resolve(inputPath.relativize(i)),isFirst);
            isFirst=false;
        }
        for(Path i: paths)if(Main.optional||!order.optional.contains(i))
        {
            parseGeneral(i,outputPath.resolve(inputPath.relativize(i)),isFirst);
            isFirst=false;
        }
        for(Path i: order.after)if(Main.optional||!order.optional.contains(i))
        {
            parseGeneral(i,outputPath.resolve(inputPath.relativize(i)),isFirst);
            isFirst=false;
        }
    }

    private static void parseGeneral(Path inputPath, Path outputPath,boolean isFirst) throws IOException
    {
        if(!inputPath.toFile().exists())return;
        if (inputPath.toFile().isFile())
        {
            parseFile(inputPath, outputPath,isFirst);
        }
        else
        {
            parseFolder(inputPath, outputPath);
        }
    }
    private static void constructMenu(Path outputPath)
    {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("<pre>\n");
        for(FileEntity fileEntity:files)
        {
            stringBuilder.append(format("%s%s\n",
                    " ".repeat(4*Math.max(0, fileEntity.depth - outputPath.getNameCount())),
                    format(linkFormat,outputPath.getParent().relativize(fileEntity.path),fileEntity.title)));
        }
        stringBuilder.append("</pre>\n");
        outputPath.getParent().toFile().mkdirs();
        try(FileWriter writer=new FileWriter(outputPath.toFile()))
        {
            writer.write(format(htmlFormat,"Menu", stringBuilder));
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    private static void generateChapters(Path index)
    {
        for(int i=0;i<files.size();i++)
        {
            FileEntity fileEntity=files.get(i);
            StringBuilder stringBuilder=new StringBuilder();
            StringBuilder linkBuilder=new StringBuilder();
            linkBuilder.append(format(linkFormat,fileEntity.path.getParent().relativize(index),"Menu"));
            if(i>0)
            {
                linkBuilder.append(format(linkFormat,fileEntity.path.getParent().relativize(files.get(i-1).path),"Prev"));
            }
            if(i<files.size()-1)
            {
                linkBuilder.append(format(linkFormat,fileEntity.path.getParent().relativize(files.get(i+1).path),"Next"));
            }
            stringBuilder.append(format("<h3>%s</h3>\n",fileEntity.title));
            stringBuilder.append(linkBuilder);
            stringBuilder.append("<pre>\n");
            for(String s:fileEntity.content)
            {
                stringBuilder.append(s);
                stringBuilder.append('\n');
            }
            stringBuilder.append("</pre>\n");
            stringBuilder.append(linkBuilder);
            fileEntity.path.getParent().toFile().mkdirs();
            try(FileWriter writer=new FileWriter(fileEntity.path.toFile()))
            {
                writer.write(format(htmlFormat,fileEntity.title,stringBuilder));
            }
            catch(Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    public static void parseRoot(Path inputPath, Path outputPath) throws IOException
    {
        parseFolder(inputPath,outputPath.resolve("content"));
        Path index=outputPath.resolve("index.html");
        constructMenu(index);
        generateChapters(index);
    }
}

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

public class Web
{
    private record FileEntity(String title,List<String> content,Path path,int depth){}
    private static final TreeNode<FileEntity> files=new TreeNode<>(null,null);
    private static final String htmlFormat= """
            <!DOCTYPE html>
            <html>
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=yes, maximum-scale=5.0">
                    <style>
                        body{
                            overflow-wrap: break-word;
                            word-break: break-word;
                            max-width: 100%%;
                            overflow-x: hidden;
                        }
                        pre, code {
                            white-space: pre-wrap;
                            background: #f4f4f4;
                            padding: 10px;
                            border-radius: 4px;
                            display: block;
                            overflow-x: hidden;
                        }
                        .hidden
                        {
                            color: #f4f4f4;
                        }
                    </style>
                    <title>%s</title>
                </head>
                <body>
            %s
                </body>
            </html>""";
    private static final String linkFormat="<a href=\"%s\">%s</a>";
    private static final String colorFormat="<span style=\"color:%s;\">%s</span>";
    private static final String hiddenFormat="<span class=\"hidden\">%s</span>";
    private static String parseString(String line)
    {
        String cur=line;
        if(CfgLoader.replaceChars)
        {
            for(Map.Entry<String, CharItem> entry:CfgLoader.charMap.entrySet())
            {
                String placeholder=CfgLoader.left+entry.getKey()+CfgLoader.right;
                String coloredName;
                if(entry.getValue().color==null)coloredName=entry.getValue().name;
                else if(entry.getValue().color.isEmpty())coloredName=format(hiddenFormat,entry.getValue().name);
                else coloredName=format(colorFormat,entry.getValue().color,entry.getValue().name);
                cur=cur.replaceAll(placeholder, coloredName);
            }
        }
        return cur;
    }

    private static FileEntity parseFile(Path inputPath, Path outputPath, int depth) throws IOException
    {
        if(!inputPath.toString().endsWith(".txt"))return null;
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
        fileName=fileName.substring(0,fileName.length()-4)+".html";
        return new FileEntity(title,content,outputPath.getParent().resolve(fileName),depth);
    }

    private static void parseFolder(Path inputPath, Path outputPath, int depth, TreeNode<FileEntity> node) throws IOException
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
        TreeNode<FileEntity> parent;
        int newDepth=depth;
        int startIndex;
        if(hasInfo) {
            FileEntity info=parseFile(
                    inputPath.resolve("info.txt"),
                    outputPath.resolve("info.txt"),
                    depth);
            parent=node.addChild(info);
            newDepth++;
            startIndex=1;
        } else {
            parent = node;
            startIndex=0;
        }
        for(int i=startIndex;i<validPaths.size();i++)
        {
            parseGeneral(validPaths.get(i),outputPath.resolve(inputPath.relativize(validPaths.get(i))),newDepth,parent);
        }
    }

    private static void parseGeneral(Path inputPath, Path outputPath,int depth, TreeNode<FileEntity> node) throws IOException
    {
        if(!inputPath.toFile().exists())return;
        if (inputPath.toFile().isFile())
        {
            FileEntity file=parseFile(inputPath,outputPath,depth);
            if(file!=null)node.addChild(file);
        }
        else
        {
            parseFolder(inputPath, outputPath,depth,node);
        }
    }
    private static void constructMenu(Path outputPath)
    {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("<pre>\n");
        TreeNode<FileEntity> node=files.getNext();
        while(node!=null)
        {
            stringBuilder.append(format("%s%s\n",
                    " ".repeat(4*node.content().depth),
                    format(linkFormat,outputPath.getParent().relativize(node.content().path),node.content().title)));
            node=node.getNext();
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
        TreeNode<FileEntity> cur=files.getNext();
        TreeNode<FileEntity> last=null;
        TreeNode<FileEntity> next;
        while(cur!=null)
        {
            FileEntity fileEntity=cur.content();
            StringBuilder stringBuilder=new StringBuilder();
            StringBuilder linkBuilder=new StringBuilder();
            linkBuilder.append(format(linkFormat,fileEntity.path.getParent().relativize(index),"Menu"));
            if(last!=null)
            {
                linkBuilder.append(format(linkFormat,fileEntity.path.getParent().relativize(last.content().path),"Prev"));
            }
            next=cur.getNext();
            if(next!=null)
            {
                linkBuilder.append(format(linkFormat,fileEntity.path.getParent().relativize(next.content().path),"Next"));
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
            last=cur;
            cur=next;
        }
    }
    public static void parseRoot(Path inputPath, Path outputPath) throws IOException
    {
        parseFolder(inputPath,outputPath.resolve("content"),0,files);
        Path index=outputPath.resolve("index.html");
        constructMenu(index);
        generateChapters(index);
    }
}

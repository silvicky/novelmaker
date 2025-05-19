package io.silvicky.novel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class Main
{
    public static Set<Path> globalIgnore=new HashSet<>();
    public static void help()
    {
        System.out.println("Usage: java -jar novelmaker.jar [-h] [-s] [-i <input_path>] [-o <output_path>] [-c <config_path>]");
    }
    public static void parseString(String line, Writer writer) throws IOException
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
        writer.write(cur);
        writer.append('\n');
    }
    public static void parseFile(Path file, Writer writer) throws IOException
    {
        if(!file.toString().endsWith(".txt"))return;
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file.toFile()));
        String cur;
        while(true)
        {
            cur= bufferedReader.readLine();
            if(cur==null)break;
            parseString(cur,writer);
        }
        writer.append('\n');
    }
    public static void parseFolder(Path inputPath, Writer writer) throws IOException
    {
        List<Path> paths=new ArrayList<>();
        Order order=new Order(inputPath);
        for(File i: Objects.requireNonNull(inputPath.toFile().listFiles()))
        {
            Path path=i.toPath().toAbsolutePath();
            if(globalIgnore.contains(path)||order.before.contains(path)||order.after.contains(path)||order.ignore.contains(path))continue;
            paths.add(path);
        }
        for(Path i: order.before)parseGeneral(i,writer);
        for(Path i: paths)parseGeneral(i,writer);
        for(Path i: order.after)parseGeneral(i,writer);
        writer.append('\n');
    }
    public static void parseGeneral(Path path, Writer writer) throws IOException
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
    public static void main(String[] args) throws IOException
    {
        Iterator<String> it=Arrays.stream(args).iterator();
        Path inputPath=Path.of(""),outputPath=null,configPath=null;
        boolean help=false;
        boolean screenOutput=false;
        while(it.hasNext())
        {
            String s=it.next();
            switch (s)
            {
                case "-i" -> inputPath = Path.of(it.next());
                case "-o" -> outputPath = Path.of(it.next());
                case "-c" -> configPath = Path.of(it.next());
                case "-h" -> help = true;
                case "-s" -> screenOutput = true;
                default -> throw new RuntimeException("Unknown argument: "+s);
            }
        }
        if(outputPath==null)outputPath=inputPath.resolve("result.txt");
        globalIgnore.add(outputPath.toAbsolutePath());
        outputPath.toFile().delete();
        if(help)
        {
            help();
            return;
        }
        Path path=configPath==null?inputPath.resolve("novelmaker.json"):configPath;
        CfgLoader.load(path);
        OutputStream outputStream;
        if(screenOutput) outputStream=System.out;
        else outputStream=new FileOutputStream(outputPath.toString());
        Writer writer=new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        parseFolder(inputPath,writer);
        writer.flush();
    }
}

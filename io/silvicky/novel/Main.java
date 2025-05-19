package io.silvicky.novel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class Main
{
    public static void help()
    {
        System.out.println("Usage: java -jar novelmaker.jar [-h] [-s] [-i <input_path>] [-o <output_path>] [-c <config_path>]");
    }
    public static String parseString(String line)
    {
        if(!CfgLoader.replaceChars)return line;
        String cur=line;
        for(Map.Entry<Integer,String> entry:CfgLoader.charMap.entrySet())
        {
            String placeholder=CfgLoader.left+entry.getKey()+CfgLoader.right;
            cur=cur.replaceAll(placeholder, entry.getValue());
        }
        return cur;
    }
    public static void parseFile(Path file, Writer writer) throws IOException
    {
        BufferedReader bufferedReader=new BufferedReader(new FileReader(file.toFile()));
        String cur;
        while(true)
        {
            cur= bufferedReader.readLine();
            if(cur==null)return;
            writer.write(parseString(cur));
            writer.append('\n');
        }
    }
    public static void work(Path inputPath, Writer writer)
    {

    }
    public static void main(String[] args)
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
        if(help)
        {
            help();
            return;
        }
        try
        {
            Path path=configPath==null?inputPath.resolve("novelmaker.json"):configPath;
            CfgLoader.load(path);
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        OutputStream outputStream;
        try
        {
            if(screenOutput) outputStream=System.out;
            else outputStream=new FileOutputStream(outputPath.toString());
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        Writer writer=new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        work(inputPath,writer);
    }
}

package io.silvicky.novel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class Main
{
    public static Set<Path> globalIgnore=new HashSet<>();
    public static boolean title=false;
    public static void help()
    {
        System.out.println("Usage: java -jar novelmaker.jar [-h] [-s] [-t] [-i <input_path>] [-o <output_path>] [-c <config_path>]");
        System.out.println("Options:");
        System.out.println("-h: Open help");
        System.out.println("-s: Output onto screen(System.out)");
        System.out.println("-t: Output first line only(making a menu)");
        System.out.println("-i: Specifying input path, a folder(default: .)");
        System.out.println("-o: Specifying output path, a file(default: <input_path>/result.txt)");
        System.out.println("-o: Specifying config path, a file(default: <input_path>/novelmaker.json)");
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
            if(title)return;
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
            if(path.toFile().exists())paths.add(path);
        }
        paths.sort((o1, o2) ->
        {
            if(o1.toFile().isFile()&&o2.toFile().isDirectory())return -1;
            if(o1.toFile().isDirectory()&&o2.toFile().isFile())return 1;
            return o1.getFileName().compareTo(o2.getFileName());
        });
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
        Path inputPath=Path.of(".").toAbsolutePath(),outputPath=null,configPath=null;
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
                case "-t" -> title = true;
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
        if(configPath==null)configPath=inputPath.resolve("novelmaker.json");
        CfgLoader.load(configPath);
        OutputStream outputStream;
        if(screenOutput) outputStream=System.out;
        else outputStream=new FileOutputStream(outputPath.toString());
        Writer writer=new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        parseFolder(inputPath,writer);
        writer.flush();
    }
}

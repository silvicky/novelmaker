package io.silvicky.novel.novel;

import io.silvicky.novel.novel.output.PlainText;
import io.silvicky.novel.novel.output.Web;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

import static io.silvicky.novel.util.Util.deleteFolder;

public class Main
{
    public static Set<Path> globalIgnore=new HashSet<>();
    public static boolean title=false;
    public static boolean optional=false;
    public static void help()
    {
        System.out.println("Usage: java -jar novelmaker.jar [-h] [-w] [-s] [-t] [-O] [-i <input_path>] [-o <output_path>] [-c <config_path>]");
        System.out.println("Options:");
        System.out.println("-h: Open help");
        System.out.println("-w: Output as a website");
        System.out.println("-s: Output onto screen(System.out)");
        System.out.println("-t: Output first line only(making a menu)");
        System.out.println("-O: Print also optional parts");
        System.out.println("-i: Specifying input path, a folder(default: .)");
        System.out.println("-o: Specifying output path, a file(default: <input_path>/result.txt) or a folder(default: <input_path>/web)");
        System.out.println("-c: Specifying config path, a file(default: <input_path>/novelmaker.json)");
    }

    public static void main(String[] args) throws IOException
    {
        Iterator<String> it=Arrays.stream(args).iterator();
        Path inputPath=Path.of(".").toAbsolutePath(),outputPath=null,configPath=null;
        boolean screenOutput=false;
        boolean webOutput=false;
        while(it.hasNext())
        {
            String s=it.next();
            switch (s)
            {
                case "-i" -> inputPath = Path.of(it.next());
                case "-o" -> outputPath = Path.of(it.next());
                case "-c" -> configPath = Path.of(it.next());
                case "-h" ->
                {
                    help();
                    return;
                }
                case "-s" -> screenOutput = true;
                case "-w" -> webOutput = true;
                case "-t" -> title = true;
                case "-O" -> optional = true;
                default -> throw new RuntimeException("Unknown argument: "+s);
            }
        }
        if(configPath==null)configPath=inputPath.resolve("novelmaker.json");
        CfgLoader.load(configPath);
        if(webOutput)
        {
            if(outputPath==null)outputPath=inputPath.resolve("web");
            globalIgnore.add(outputPath.toAbsolutePath());
            deleteFolder(outputPath);
            Web.parseRoot(inputPath,outputPath);
            return;
        }
        OutputStream outputStream;
        if(screenOutput) outputStream=System.out;
        else
        {
            if(outputPath==null)outputPath=inputPath.resolve("result.txt");
            globalIgnore.add(outputPath.toAbsolutePath());
            deleteFolder(outputPath);
            outputPath.getParent().toFile().mkdirs();
            outputStream = new FileOutputStream(outputPath.toString());
        }
        Writer writer=new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        PlainText.parseRoot(inputPath,writer);
        writer.flush();
    }
}

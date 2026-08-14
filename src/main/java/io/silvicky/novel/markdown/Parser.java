package io.silvicky.novel.markdown;

import java.util.Iterator;
import java.util.List;

import static java.lang.String.format;

public class Parser
{
    public static String parseContent(String input)
    {
        //TODO rewrite all
        return null;
    }
    public static String parseParagraph(String input)
    {
        if (input.isEmpty()) return input;
        int sharpCnt = 0;
        for (int i = 0; i < input.length(); i++)
        {
            if (input.charAt(i) == '#') sharpCnt++;
            else break;
        }
        if (sharpCnt == 0 ||sharpCnt>6)
        {
            return parseContent(input);
        }
        return format("<h%d>%s</h%d>",sharpCnt,parseContent(input.substring(sharpCnt)),sharpCnt);
    }
    public static String parse(String input)
    {
        List<String> lines= input.lines().toList();
        Iterator<String> it= lines.iterator();
        StringBuilder ret=new StringBuilder();
        StringBuilder curParagraph=new StringBuilder();
        while(it.hasNext())
        {
            String curLine=it.next();
            if(curLine.isEmpty())
            {
                ret.append(format("<p>%s</p>",parseParagraph(curParagraph.toString())));
                curParagraph=new StringBuilder();
                continue;
            }
            curParagraph.append(' ');
            curParagraph.append(curLine);
        }
        if(!curParagraph.isEmpty())ret.append(format("<p>%s</p>",parseParagraph(curParagraph.toString())));
        return ret.toString();
    }
}

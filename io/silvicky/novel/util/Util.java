package io.silvicky.novel.util;

import java.util.List;

public class Util
{
    public static <T> void addNonNull(List<T> list, T element)
    {
        if(element==null)return;
        list.add(element);
    }
}

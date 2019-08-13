package com.power.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * List工具类
 * @author : xuyunfeng
 * @date :   2019/8/13 14:13
 */
public class ListUtils {
    public static List<Map<String,String>> removeDuplicates (List<Map<String,String>> list){
        HashSet<Map<String,String>> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        return list;
    }
}

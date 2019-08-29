package com.power.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * List工具类
 * @author : xuyunfeng
 * @date :   2019/8/13 14:13
 */
public class ListUtils {
    /**
     * list去重静态方法
     * @param list List<Map<String,String>> list列表
     * @return 去重后的list
     */
    public static List<Map<String,String>> removeMapDuplicates (List<Map<String,String>> list){
        HashSet<Map<String,String>> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    /**
     * list去重静态方法
     * @param list List<String> list列表
     * @return 去重后的list
     */
    public static List<String> removeStringDuplicates (List<String> list){
        HashSet<String> set = new HashSet<>(list);
        return new ArrayList<>(set);
    }

}

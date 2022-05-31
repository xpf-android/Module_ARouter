package com.xpf.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局路径记录器(根据子模块分组)
 */
public class RecordPathManager {

    // key:组名"order" value: order子模块下的所有Activity路径信息
    private static Map<String, List<PathBean>> groupMap = new HashMap<>();

    /**
     * 将路径信息加入全局Map
     *
     * @param groupName 组名，如："personal"
     * @param pathName  路径名，如："Personal_MainActivity"
     * @param clazz     类对象，如：Personal_MainActivity.class
     */
    public static void joinGroup(String groupName, String pathName, Class<?> clazz) {
        List<PathBean> list = groupMap.get(groupName);
        if (list == null) {
            list = new ArrayList<>();
            list.add(new PathBean(pathName, clazz));
            groupMap.put(groupName, list);
        } else {
            for (PathBean pathBean : list) {
                if (!pathName.equals(pathBean.getPath())) {
                    list.add(new PathBean(pathName, clazz));
                    groupMap.put(groupName, list);
                }

            }
        }
    }


    /**
     * 根据组名和路径名获取类对象，达到跳转目的
     * @param groupName
     * @param pathName
     * @return
     */
    public static Class<?> getTargetClass(String groupName, String pathName) {
        List<PathBean> list = groupMap.get(groupName);
        if (list == null) return null;
        for (PathBean pathBean : list) {
            // 比较路径名时忽略大小写
            if (pathName.equalsIgnoreCase(pathBean.getPath())) {
                return pathBean.getClazz();
            }
        }
        return null;
    }

}

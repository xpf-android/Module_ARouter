package com.xpf.module_arouter;

public class Client {
    public static void main(String[] args) {
//        String path = "///";
        String path = "/app/M/ainActivity";
        test(path);

    }

    private static boolean test(String path) {
        if (path == null || !path.startsWith("/")) {
            return false;
        }

        //比如开发者代码为：path = "/MainActivity"
        if (path.lastIndexOf("/") == 0) {//只有一条/
            return false;
        }
        //todo 该方法表示在索引1之前，最后一次出现/的位置
        int index = path.lastIndexOf("/",1);
        System.out.println(index);//0
        //从第一个/到第二个/中间截取出组名group
        //substring(a,b) 从索引a到b截取字符串，左闭右开
        //path.indexOf("/",1),path从索引1开始，第一次出现/的索引，在此处为2
        /*String finalGroup = path.substring(1,path.indexOf("/"));
        System.out.println(finalGroup);*/
        return false;
    }
}

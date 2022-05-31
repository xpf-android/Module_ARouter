package com.xpf.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.xpf.annotation.model.RouterBean;
import com.xpf.api.core.ARouterLoadGroup;
import com.xpf.api.core.ARouterLoadPath;
import com.xpf.api.core.BundleManager;

public class RouterManager {

    //路由组名
    private String group;
    //路由Path路径
    private String path;
    private static RouterManager instance;
    //LruCache缓存 key：group value：路由组Group加载接口
    private LruCache<String, ARouterLoadGroup> groupLruCache;
    //LruCache缓存 key：path value：路由Path路径加载接口
    private LruCache<String, ARouterLoadPath> pathLruCache;
    //APT生成类文件后缀名(包名拼接)
    private static final String GROUP_FILE_PREFIX_NAME = "ARouter$$Group$$";

    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    private RouterManager() {
        groupLruCache = new LruCache<>(163);
        pathLruCache = new LruCache<>(163);
    }

    //传递路由的地址
    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("未按照规范配置，如：/app/MainActivity");
        }
        //获取组名(模块名)
        group = subFromPath2Group(path);
        //此时的path和group均已接受过检查，经受住了考验。
        this.path = path;
        return new BundleManager();
    }

    /**
     * 从路由path中截取组名group
     */
    private String subFromPath2Group(String path) {
        //能执行到这里，path一定是以/开头的
        //如果path中只有一条/ 如：path = "/MainActivity"
        //如果path中两条/相连 如：path = "//MainActivity"
        if (path.lastIndexOf("/") == 0 || path.lastIndexOf("/") == 1) {
            throw new IllegalArgumentException("未按照规范配置，如：/app/MainActivity");
        }
        //不止两条/ 如：path = "/app/MainActivity/"
        if (path.substring(1, path.lastIndexOf("/")).contains("/")) {
            throw new IllegalArgumentException("未按照规范配置，如：/app/MainActivity");
        }
        //从第一条/到第二条/之间截取
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        //此处判断其实可以省略，因为subFromPath2Group方法最开始已经判断过，能走到这里
        //已排除这种可能性
        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("未按照规范配置，如：/app/MainActivity");
        }

        //架构师定义规范，让开发者遵循！！！，这里的规范指的是组名group不能随便填写

        return finalGroup;
    }

    /**
     * 开始跳转
     *
     * @param context       上下文
     * @param bundleManager 参数管理
     * @param code          resultCode或requestCode, 取决于isResult
     * @return 普通跳转可以忽略，用于跨模块CALL接口
     */
    public Object navigation(Context context, BundleManager bundleManager, int code) {
        //todo 跨模块的路由的核心原理1 跨模块获取路由Group文件类名
        String groupClassName = context.getPackageName() + ".apt." + GROUP_FILE_PREFIX_NAME + group;
        Log.e("netEasy modular",groupClassName);
        try {
            //读取路由组Group类文件
            //todo
            // 懒加载：只有需要跳转的时候，才会去加载对应的类文件
            // 缓存：当再次跳转的时候，不需要去重复加载过程
            ARouterLoadGroup groupLoad = groupLruCache.get(group);
            if (groupLoad == null) {
                //todo 跨模块路由的核心原理2 通过Class.forName等操作获取路由Group文件的对象
                //加载APT路由组Group类文件，如：ARouter$$Group$$personal
                Class<?> clazz = Class.forName(groupClassName);
                //初始化路由Group类文件
                //这里不强转为ARouterLoadGroup接口具体的实现类，如ARouter$$Group$$personal，是为了
                //兼容各个模块都适用这段代码
                 groupLoad = (ARouterLoadGroup) clazz.newInstance();
                 groupLruCache.put(group,groupLoad);
            }
            //判断
            if (groupLoad.loadGroup().isEmpty()) {
                throw new RuntimeException("路由表Group加载失败！");
            }
            //读取路由Path路径类文件缓存
            ARouterLoadPath pathLoad = pathLruCache.get(path);
            if (pathLoad == null) {
                //todo 跨模块路由的核心原理3 通过路由组Group类文件，获取路由Path类文件
                //通过Group加载接口，获取path加载接口
                Class<? extends ARouterLoadPath> clazz = groupLoad.loadGroup().get(group);
                //初始化具体path类文件如：ARouter$$Path$$personal
                if (clazz != null) pathLoad = clazz.newInstance();
                if (pathLoad!=null) pathLruCache.put(path,pathLoad);
            }
            if (pathLoad != null) {//代码健壮性
                if (pathLoad.loadPath().isEmpty()) {
                    throw new RuntimeException("路由表Path加载失败!");
                }
                RouterBean routerBean = pathLoad.loadPath().get(path);
                if (routerBean != null) {
                    switch (routerBean.getType()) {
                        case ACTIVITY:
                            //routerBean.getClazz()就是路由目标的class文件
                            Intent intent = new Intent(context,routerBean.getClazz());
                            intent.putExtras(bundleManager.getBundle());
                            //注意：startActivityForResult ---> setResult  code 为requestCode
                            if (bundleManager.isResult()) {
                                ((Activity) context).setResult(code,intent);
                                ((Activity) context).finish();
                            }

                            if (code > 0) {//跳转的时候需要回调
                                ((Activity)context).startActivityForResult(intent,code,bundleManager.getBundle());
                            } else {
                                context.startActivity(intent,bundleManager.getBundle());
                            }

                            break;
                        case CALL:
                            //todo 返回的是接口Call的实现类
                            return routerBean.getClazz().newInstance();
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

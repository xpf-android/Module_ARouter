package com.xpf.api.core;




import com.xpf.annotation.model.RouterBean;

import java.util.Map;


/**
 * 路由组group对应的详细path加载数据接口
 * 比如：app分组对应有哪些类需要加载
 */
public interface ARouterLoadPath {

    /**
     * 加载路由组group中的path详细数据
     * 比如：app分组下有这些信息
     * @return key: "app/MainActivity", value:MainActivity信息封装到RouterBean对象中
     */
    Map<String, RouterBean> loadPath();
}

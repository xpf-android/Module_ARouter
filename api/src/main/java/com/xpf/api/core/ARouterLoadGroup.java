package com.xpf.api.core;

import java.util.Map;

/**
 * 路由组group对外提供加载数据接口
 */
public interface ARouterLoadGroup {

    /**
     * 加载路由组group数据
     * 比如："app", ARouter$$Path$$app.class(实现了ARouterLoadPath接口)
     * @return key: "app", value: "aoo"分组对应的路由详细对象类
     */
    Map<String, Class<? extends ARouterLoadPath>> loadGroup();
}

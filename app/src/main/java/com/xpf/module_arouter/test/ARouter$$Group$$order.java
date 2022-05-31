package com.xpf.module_arouter.test;

import com.xpf.api.core.ARouterLoadGroup;
import com.xpf.api.core.ARouterLoadPath;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟ARouter路由器组文件，对应的路径
 */
public class ARouter$$Group$$order implements ARouterLoadGroup {


    @Override
    public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        groupMap.put("order", ARouter$$Path$$order.class);
        return groupMap;
    }
}

package com.xpf.module_arouter.test;

import com.xpf.annotation.model.RouterBean;
import com.xpf.api.core.ARouterLoadPath;
import com.xpf.order.Order_MainActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 模拟ARouter路由器的组文件对应的路径
 */
public class ARouter$$Path$$order implements ARouterLoadPath {
    /**
     * 返回对应组(模块)名，所有被注解过的Activity的RouterBean对象的集合
     * @return
     */
    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String, RouterBean> pathMap = new HashMap<>();
        pathMap.put("/order/Order_MainActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
                Order_MainActivity.class,
                "/order/Order_MainActivity",
                "order"));

        /*pathMap.put("/order/Order_ListActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
                Order_MainActivity.class,
                "/order/Order_ListActivity",
                "order"));*/

        return pathMap;
    }
}

package com.xpf.order.impl;

import com.xpf.annotation.ARouter;
import com.xpf.common.order.OrderDrawable;
import com.xpf.order.R;

@ARouter(path = "/order/getDrawable")
public class OrderDrawableImpl implements OrderDrawable {
    @Override
    public int getDrawable() {
        return R.drawable.icon_mv_p;
    }
}

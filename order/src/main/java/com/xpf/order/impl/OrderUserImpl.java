package com.xpf.order.impl;


import com.xpf.annotation.ARouter;
import com.xpf.common.user.BaseUser;
import com.xpf.common.user.IUser;
import com.xpf.order.model.UserInfo;

/**
 * personal模块实现的内容
 */
@ARouter(path = "/order/getUserInfo")
public class OrderUserImpl implements IUser {

    @Override
    public BaseUser getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("迷途小书童");
        userInfo.setAccount("netease_river");
        userInfo.setPassword("666666");
        userInfo.setVipLevel(9);
        return userInfo;
    }
}

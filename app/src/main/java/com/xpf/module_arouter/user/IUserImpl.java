package com.xpf.module_arouter.user;


import com.xpf.annotation.ARouter;
import com.xpf.common.user.BaseUser;
import com.xpf.common.user.IUser;
import com.xpf.order.model.UserInfo;

/**
 * personal模块实现的内容
 */
@ARouter(path = "/app/getUserInfo")
public class IUserImpl implements IUser {

    @Override
    public BaseUser getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setName("彭老师");
        userInfo.setAccount("netease_simon");
        userInfo.setPassword("666666");
        userInfo.setVipLevel(9);
        return userInfo;
    }
}

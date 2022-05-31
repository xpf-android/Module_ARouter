package com.xpf.order.impl;


import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xpf.annotation.ARouter;
import com.xpf.common.order.OrderAddress;
import com.xpf.common.order.OrderBean;
import com.xpf.common.utils.Cons;
import com.xpf.order.services.OrderServices;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 订单模块对外暴露接口实现类，其他模块可以获取返回数据
 */
@ARouter(path = "/order/getOrderBean")
public class OrderAddressImpl implements OrderAddress {

    private final static String BASE_URL = "http://apis.juhe.cn/ip/ipNewV3/";

    @Override
    public OrderBean getOrderBean(String key, String ip) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        OrderServices host = retrofit.create(OrderServices.class);

        // Retrofit GET同步请求
        Call<ResponseBody> call = host.get(ip, key);

        retrofit2.Response<ResponseBody> response = call.execute();
        if (response != null && response.body() != null) {
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            OrderBean orderBean = jsonObject.toJavaObject(OrderBean.class);
            Log.e(Cons.TAG ,orderBean.toString());
            return orderBean;
        }
        return null;
    }
}

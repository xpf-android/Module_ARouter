package com.xpf.api.core;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xpf.api.RouterManager;

/**
 * 参数管理类
 */
public class BundleManager {
    private Bundle bundle = new Bundle();
    //是否回调setResult(),因为有可能是startActivityForResult()
    private boolean isResult;

    public Bundle getBundle() {
        return bundle;
    }

    public boolean isResult() {
        return isResult;
    }

    //对外提供传参方法
    //@NonNull不允许传null，@Nullable可以传null
    public BundleManager withString(@NonNull String key, @Nullable String value) {
        bundle.putString(key,value);
        return this;
    }

    //示例代码，需要架构师拓展
    public BundleManager withResultString(@NonNull String key,@Nullable String value) {
        bundle.putString(key,value);
        isResult = true;
        return this;
    }

    public BundleManager withBoolean(@NonNull String key,boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withInt(@NonNull String key,int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBundle(@NonNull Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    //startActivity
    public Object navigation(Context context) {
        return navigation(context,-1);
    }

    //startActivityForResult
    //这里的code可能是resultCode,也肯能是requestCode，取决于isResult
    public Object navigation(Context context,int code) {
        return RouterManager.getInstance().navigation(context,this,code);
    }
}

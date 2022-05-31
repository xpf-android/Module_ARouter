package com.xpf.personal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xpf.annotation.ARouter;
import com.xpf.annotation.Parameter;
import com.xpf.annotation.model.RouterBean;
import com.xpf.api.ParameterManager;
import com.xpf.api.RouterManager;
import com.xpf.api.core.ARouterLoadPath;
import com.xpf.common.utils.Cons;

import java.util.Map;


@ARouter(path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends AppCompatActivity {

    @Parameter
    String name;

    @Parameter
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);

        //懒加载方式，跳转到哪个类就加载哪个类
        ParameterManager.getInstance().loadParameter(this);

        Log.e(Cons.TAG,"personal接收参数值 name = " + name);

    }

    public void jumpApp(View view) {
        RouterManager.getInstance()
                .build("/app/MainActivity")
                .withString("name","simon from personal")
                .navigation(this);
    }

    public void jumpOrder(View view) {
        RouterManager.getInstance()
                .build("/order/Order_MainActivity")
                .withResultString("call","i'm back")
                .navigation(this);
    }
}

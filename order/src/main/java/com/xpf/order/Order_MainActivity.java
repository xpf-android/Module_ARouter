package com.xpf.order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xpf.annotation.ARouter;
import com.xpf.annotation.Parameter;
import com.xpf.api.ParameterManager;
import com.xpf.api.RouterManager;
import com.xpf.api.core.ParameterLoad;
import com.xpf.common.utils.Cons;

@ARouter(path = "/order/Order_MainActivity")
public class Order_MainActivity extends AppCompatActivity {

    @Parameter
    String name;
    @Parameter(name = "agex")
    int age = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);
        Log.e(Cons.TAG,"/order/Order_MainActivity");

        //此段代码被封装到ParameterManager中
        /*
        ParameterLoad parameterLoad = new Order_MainActivity$$Parameter();
        parameterLoad.loadParameter(this);
        */
        //懒加载方式，跳转到哪加载哪个类
        ParameterManager.getInstance().loadParameter(this);
        Log.e(Cons.TAG," order 接收参数 name >>> " + name+ " / age >>> " + age);

    }

    public void jumpApp(View view) {
        /*RouterManager.getInstance()
                .build("/app/MainActivity")
                .withString("name","simon from order")
                .navigation(this);*/

        //withResult方法中会设置isResult为true，会触发
        //if (bundleManager.isResult()) {
        //        ((Activity) context).setResult(code,intent);
        //        ((Activity) context).finish();
        //}
        RouterManager.getInstance()
                .build("/app/MainActivity")
                .withResultString("call","i am back")
                .navigation(this);

    }

    public void jumpPersonal(View view) {
        RouterManager.getInstance()
                .build("/personal/Personal_MainActivity")
                .withString("name","simon from order")
                .navigation(this);
    }


}

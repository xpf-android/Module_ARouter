package com.xpf.module_arouter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.xpf.annotation.ARouter;
import com.xpf.annotation.Parameter;
import com.xpf.annotation.model.RouterBean;
import com.xpf.api.ParameterManager;
import com.xpf.api.RouterManager;
import com.xpf.api.core.ARouterLoadPath;
import com.xpf.common.order.OrderAddress;
import com.xpf.common.order.OrderBean;
import com.xpf.common.order.OrderDrawable;
import com.xpf.common.user.IUser;
import com.xpf.common.utils.Cons;
import com.xpf.module_arouter.test.ARouter$$Group$$order;
import com.xpf.module_arouter.test.ARouter$$Group$$personal;
import com.xpf.order.impl.OrderUserImpl;

import java.io.IOException;
import java.util.Map;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {
    @Parameter
    String name;
    @Parameter(name = "agex")
    int age = 1;


    @Parameter
    boolean isSuccess;

    @Parameter(name = "netease")
    String object;

    @Parameter(name = "/order/getDrawable")
    OrderDrawable drawable;

    @Parameter(name = "/order/getUserInfo")
    IUser iUser;


    @Parameter(name = "/order/getOrderBean")
    OrderAddress orderAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*name = getIntent().getStringExtra("name");
        age = getIntent().getIntExtra("agex",age);*/




        if (BuildConfig.isRelease) {
            Log.d(Cons.TAG, "当前为：集成化模式，除app可运行，其它子模块都是Android Library");
        } else {
            Log.d(Cons.TAG, "当前为：组件化模式，app/order/personal等子模块都可独立运行");
        }

        // 获取传递参数值
//        name = getIntent().getStringExtra("name");
//        age = getIntent().getIntExtra("age",age);

        ParameterManager.getInstance().loadParameter(this);
        Log.e(Cons.TAG,"app接收参数：name= " + name);


        ImageView img = findViewById(R.id.img);
        img.setImageResource(drawable.getDrawable());

        String string = iUser.getUserInfo().toString();
        Log.e(Cons.TAG,string);


        // 测试获取接口通信
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OrderBean orderBean = orderAddress.getOrderBean("18682ed3411dd8e8768cdd38d901afe1", "192.168.28.124");
                    Log.e(Cons.TAG, orderBean.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    /**
     * todo 跳转采用startActivityForResult
     * 需要注意的是，当从Order_MainActivity通过startActivity跳转回来的时候，默认是创建了一个新的MainActivity
     * 如果想回到是原来的MainActivity,需要设置启动模式为singleTask
     */
    public void jumpOrder(View view) {
        /*//最终集成化模式，所有子模块APT生成的类文件都会打包到apk中
        ARouter$$Group$$order loadGroup = new ARouter$$Group$$order();
        Map<String, Class<? extends ARouterLoadPath>> groupMap = loadGroup.loadGroup();
        //app--->order
        Class<? extends ARouterLoadPath> clazz = groupMap.get("order");
        //类加载技术
        try {
            ARouterLoadPath path = clazz.newInstance();
            Map<String, RouterBean> pathMap = path.loadPath();
            //获取/order/Order_MainActivity
            RouterBean routerBean = pathMap.get("/order/Order_MainActivity");
            if (routerBean != null) {
                Intent intent = new Intent(this, routerBean.getClazz());
                intent.putExtra("name", "simon");
                intent.putExtra("agex", 20);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //采用startActivity方式跳转
        /*RouterManager.getInstance()
                .build("/order/Order_MainActivity")
                .withString("name","simon form app")
                .withInt("agex",age)
                .navigation(this);*/
        //采用startActivityForResult方式跳转
        RouterManager.getInstance()
                .build("/order/Order_MainActivity")
                .withString("name","simon from app")
                .navigation(this,163);
    }

    /**
     * 跳转至personal的Personal_MainActivity
     */
    public void jumpPersonal(View view) {
        /*//最终集成化模式，所有子模块APT生成的类文件都会打包到apk中
        ARouter$$Group$$personal loadGroup = new ARouter$$Group$$personal();
        Map<String, Class<? extends ARouterLoadPath>> groupMap = loadGroup.loadGroup();
        //app--->personal
        Class<? extends ARouterLoadPath> clazz = groupMap.get("personal");
        //类加载技术
        try {
            ARouterLoadPath path = clazz.newInstance();
            Map<String, RouterBean> pathMap = path.loadPath();
            //获取/personal/Personal_MainActivity
            RouterBean routerBean = pathMap.get("/personal/Personal_MainActivity");
            if (routerBean != null) {
                Intent intent = new Intent(this, routerBean.getClazz());
                intent.putExtra("name", "xpf");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        RouterManager.getInstance()
                .build("/personal/Personal_MainActivity")
                .withString("name","simon form app")
                .withInt("agex",age)
                .navigation(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.e(Cons.TAG,"app回调onActivityResult：" + data.getStringExtra("call"));
        }
    }


    @Override
    public String toString() {
        return "MainActivity{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", isSuccess=" + isSuccess +
                ", object='" + object + '\'' +
                '}';
    }
}

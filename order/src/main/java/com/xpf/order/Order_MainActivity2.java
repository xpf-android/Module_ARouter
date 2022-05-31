package com.xpf.order;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xpf.annotation.ARouter;
import com.xpf.common.utils.Cons;

@ARouter(path = "/order/Order_MainActivity2")
public class Order_MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);
        Log.e(Cons.TAG,"order/Order_MainActivity");
    }

    public void jumpApp(View view) {


    }

    public void jumpPersonal(View view) {

    }
}

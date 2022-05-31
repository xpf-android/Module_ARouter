package com.xpf.module_arouter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.xpf.annotation.ARouter;
import com.xpf.annotation.Parameter;

@ARouter(path = "/app/MainActivity3")
public class MainActivity3 extends AppCompatActivity {

    @Parameter
    String password;
    @Parameter
    int gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

    }
}

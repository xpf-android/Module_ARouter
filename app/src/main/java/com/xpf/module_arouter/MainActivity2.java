package com.xpf.module_arouter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.xpf.annotation.ARouter;
import com.xpf.annotation.Parameter;

@ARouter(path = "/app/MainActivity2")
public class MainActivity2 extends AppCompatActivity {

    @Parameter
    String username;
    @Parameter
    boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

    }
}

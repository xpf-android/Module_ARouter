package com.xpf.personal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.xpf.annotation.ARouter;
import com.xpf.annotation.model.RouterBean;
import com.xpf.api.core.ARouterLoadPath;
import com.xpf.common.utils.Cons;

import java.util.Map;

@ARouter(path = "/personal/Personal_MainActivity2")
public class Personal_MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);

        Log.e(Cons.TAG,"personal/Personal_MainActivity");

    }

    public void jumpApp(View view) {

    }

    public void jumpOrder(View view) {

    }
}

package com.xpf.personal.debug;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xpf.common.utils.Config;
import com.xpf.personal.R;

public class Personal_DebugMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_debug_activity_main);
        Log.d(Config.TAG, "personal/debug/onCreate run...");
    }
}

package com.xpf.module_arouter;

import com.xpf.api.core.ParameterLoad;
import com.xpf.module_arouter.MainActivity;

public class XActivity$$Parameter implements ParameterLoad {
    @Override
    public void loadParameter(Object target) {
        //执行一次
        MainActivity t = (MainActivity) target;
        //如果和原文件不同包，就没有办法拿到没有修饰符(default)的属性名
        //循环(肯能有多个目标属性)
        t.name = t.getIntent().getStringExtra("name");
        t.age = t.getIntent().getIntExtra("age", t.age);
    }
}

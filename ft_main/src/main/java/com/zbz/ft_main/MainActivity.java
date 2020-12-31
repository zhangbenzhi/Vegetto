package com.zbz.ft_main;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zbz.lib_commen.BaseActivity;
import com.zbz.lib_commen.config.RouterConfig;

@Route(path = RouterConfig.MainModule.MainRouter)
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

}

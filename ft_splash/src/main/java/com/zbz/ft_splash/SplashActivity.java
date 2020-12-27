package com.zbz.ft_splash;

import android.os.Bundle;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zbz.lib_commen.BaseActivity;
import com.zbz.lib_commen.config.RouterConfig;

/**
 * 启动页：
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ARouter.getInstance().build(RouterConfig.SplashModule.ADRouter).navigation();
        finish();
        overridePendingTransition(0, 0);
    }
}

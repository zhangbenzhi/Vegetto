package com.zbz.vegetto;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @author 张本志
 * @date 2020/12/27 17:34
 * @description application
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        // ARouter初始化：
        ARouter.init(this);
        ARouter.openDebug();
        ARouter.openLog();
    }
}

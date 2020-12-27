package com.zbz.ft_splash;

import android.content.Intent;
import android.os.Bundle;

import com.zbz.lib_commen.BaseActivity;

/**
 * 启动页：
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, AdActivity.class));
        finish();
    }
}

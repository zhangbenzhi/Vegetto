package com.zbz.ft_splash;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zbz.lib_commen.BaseActivity;
import com.zbz.lib_commen.config.RouterConfig;
import com.zbz.lib_ui.CountDownView;

/**
 * 广告:
 */
@Route(path = RouterConfig.SplashModule.ADRouter)
public class AdActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        initView();
    }

    private void initView() {
        ImageView mAdIv = findViewById(R.id.iv_ad);
        mAdIv.setImageResource(R.drawable.img_ad);
        CountDownView mCountDownView = findViewById(R.id.view_count_down);
        mCountDownView.beginCountDown(5 * 1000, new CountDownView.OnCountDownListener() {
            @Override
            public void onFinish() {
                Toast.makeText(AdActivity.this, "倒计时结束", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

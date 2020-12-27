package com.zbz.ft_splash;

import android.os.Bundle;
import android.widget.ImageView;

import com.zbz.lib_commen.BaseActivity;

/**
 * 广告:
 */
public class AdActivity extends BaseActivity {

    private ImageView mAdIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        initView();
    }

    private void initView() {
        mAdIv = findViewById(R.id.iv_ad);
        mAdIv.setImageResource(R.drawable.img_ad);
    }
}

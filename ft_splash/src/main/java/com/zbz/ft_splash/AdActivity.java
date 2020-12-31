package com.zbz.ft_splash;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.zbz.lib_commen.BaseActivity;
import com.zbz.lib_commen.config.RouterConfig;
import com.zbz.lib_net.imageloader.VegettoImage;
import com.zbz.lib_ui.CountDownView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 广告:
 */
@Route(path = RouterConfig.SplashModule.ADRouter)
public class AdActivity extends BaseActivity {

    private List<String> mAdList = new ArrayList<>();
    private ImageView mAdIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        initView();
        getData();
    }

    private void getData() {
        mAdList.add("https://img.tukuppt.com//ad_preview/00/22/31/5c9a405170420.jpg!/fw/780");
        mAdList.add("https://img.tukuppt.com//ad_preview/00/11/77/5c994ed9d311e.jpg!/fw/780");
        mAdList.add("https://img.tukuppt.com//ad_preview/00/09/73/5c991f38dbd5f.jpg!/fw/780");

        String imageUrl = mAdList.get(new Random().nextInt(3));
        VegettoImage.with(this)
                .load(imageUrl)
                .placeholder(-1)
                .error(-1)
                .centerCrop()
                .into(mAdIv);
    }

    private void initView() {
        mAdIv = findViewById(R.id.iv_ad);
        mAdIv.setImageResource(R.drawable.img_ad);
        CountDownView mCountDownView = findViewById(R.id.view_count_down);
        mCountDownView.beginCountDown(5 * 1000, new CountDownView.OnCountDownListener() {
            @Override
            public void onFinish() {
                toMain();
            }
        });
        mCountDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMain();
            }
        });
    }

    private void toMain() {
        ARouter.getInstance().build(RouterConfig.MainModule.MainRouter).navigation();
        finish();
    }
}

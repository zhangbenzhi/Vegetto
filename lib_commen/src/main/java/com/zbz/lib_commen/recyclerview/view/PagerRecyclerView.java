package com.zbz.lib_commen.recyclerview.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author 张本志
 * @date 2020/12/31 18:56
 * @description 类似于ViewPager效果的RecyclerView
 */
public class PagerRecyclerView extends RecyclerView {

    private PagerSnapHelper pagerSnapHelper;

    public PagerRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public PagerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (pagerSnapHelper == null) {
            pagerSnapHelper = new PagerSnapHelper();
            pagerSnapHelper.attachToRecyclerView(this);
        }
    }
}

package com.zbz.lib_net.imageloader.listener;

import android.graphics.drawable.Drawable;

/**
 * @author 张本志
 * @date 2020/7/16 14:20
 * @description 图片加载监听
 */
public abstract class OnLoadImageListener<T> {

    public void onLoadStarted(Drawable placeholder) {
    }

    public abstract void onResourceReady(T t);

    public void onLoadFailed(String msg) {
    }
}
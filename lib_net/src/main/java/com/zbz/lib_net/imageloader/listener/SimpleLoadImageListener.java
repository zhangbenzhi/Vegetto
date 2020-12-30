package com.zbz.lib_net.imageloader.listener;

/**
 * @author 张本志
 * @date 2020/7/24 16:51
 * @description 仅仅是监听图片加载
 */
public interface SimpleLoadImageListener {
    boolean onResourceReady(Object resource, boolean isFirstResource);

    boolean onLoadFailed(Exception e, boolean isFirstResource);
}

package com.zbz.lib_net.imageloader.mode;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 张本志
 * @date 2020/7/16 14:20
 * @description 图片加载磁盘缓存策略
 */
@IntDef({DiskCacheMode.NOT_SET, DiskCacheMode.RESULT, DiskCacheMode.SOURCE, DiskCacheMode.NONE, DiskCacheMode.ALL})
@Retention(RetentionPolicy.SOURCE)
public @interface DiskCacheMode {
    // 不设置，设置了反而会有些缓存问题
    int NOT_SET = -1;
    // 缓存加载后的图片
    int RESULT = 0;
    // 缓存原图
    int SOURCE = 1;
    // 不缓存
    int NONE = 2;
    // 既缓存原图也缓存加载后的图片
    int ALL = 3;
}

package com.zbz.lib_net.imageloader.mode;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 张本志
 * @date 2020/7/16 14:19
 * @description 加载图片优先级：
 */
@IntDef({UmePriority.IMMEDIATE, UmePriority.HIGH, UmePriority.NORMAL, UmePriority.LOW})
@Retention(RetentionPolicy.SOURCE)
public @interface UmePriority {
    // 加载优先级
    int NORMAL = 0;
    int HIGH = 1;
    int IMMEDIATE = 2;
    int LOW = 3;
}
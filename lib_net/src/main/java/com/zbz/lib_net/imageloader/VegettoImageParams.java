package com.zbz.lib_net.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.request.target.Target;
import com.zbz.lib_net.R;
import com.zbz.lib_net.imageloader.glide.GlideUtil;
import com.zbz.lib_net.imageloader.listener.OnLoadImageListener;
import com.zbz.lib_net.imageloader.listener.SimpleLoadImageListener;
import com.zbz.lib_net.imageloader.mode.DiskCacheMode;
import com.zbz.lib_net.imageloader.mode.UmePriority;

import java.io.File;

/**
 * @author 张本志
 * @date 2020/7/15 16:18
 * @description 贝吉特图片加载参数
 */
public class VegettoImageParams {
    private static final String TAG = VegettoImageParams.class.getSimpleName();

    // 网络图片url
    String imgUrl;
    // 本地图片path
    File filePath;
    // 资源图片id
    int resourceId;
    // 设置磁盘缓存策略：
    @DiskCacheMode
    int diskCacheMode = DiskCacheMode.NOT_SET;
    // 是否跳过内存缓存：
    boolean skipMemoryCache = false;
    // 加载中占位图：
    int placeholder = R.drawable.img_loading_placeholder;
    // 加载失败占位图：
    int errorPlaceholder = R.drawable.img_error_placeholder;
    // 指定图片宽：
    int overrideWidth = Target.SIZE_ORIGINAL;
    // 指定图片高：
    int overrideHeight = Target.SIZE_ORIGINAL;
    // 是否切成圆形：
    boolean clipToCircle;
    // 圆角大小数组
    int[] rounds;
    // 将drawable转为bitmap：
    boolean asBitmap;
    // 返回GifDrawable  gif是本地资源文件的话需要设置，http/https的貌似不用设置
    boolean asGif;
    // 是否centerCrop:
    boolean centerCrop;
    // 是否fitXY:
    boolean fitXY;
    // 是否禁用动画：
    boolean dontAnimation;
    // 是否仅在缓存中获取图片
    boolean onlyRetrieveFromCache;
    // 加载返回对应的Drawable或Bitmap：
    OnLoadImageListener onLoadImageListener;
    // 加载监听：
    SimpleLoadImageListener simpleLoadImageListener;
    // ImageView
    ImageView imageView;
    // context
    Context context;
    // Priority 加载优先级
    @UmePriority
    int priority = UmePriority.NORMAL;
    //最后由图片库加载的url：
    Object loadUrl;

    VegettoImageParams() {
    }

    public Context getContext() {
        return context;
    }

    public SimpleLoadImageListener getSimpleLoadImageListener() {
        return simpleLoadImageListener;
    }

    /**
     * 加载返回File文件：
     */
    public File getFile() {
        try {
            return GlideUtil.getFile(this);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 加载返回Bitmap:
     */
    public Bitmap getBitmap() {
        try {
            return GlideUtil.getImageBitMap(this);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isOnlyRetrieveFromCache() {
        return onlyRetrieveFromCache;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public File getFilePath() {
        return filePath;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getDiskCacheMode() {
        return diskCacheMode;
    }

    public boolean isSkipMemoryCache() {
        return skipMemoryCache;
    }

    public int getPlaceholder() {
        return placeholder;
    }

    public int getErrorPlaceholder() {
        return errorPlaceholder;
    }

    public int getOverrideWidth() {
        return overrideWidth;
    }

    public int getOverrideHeight() {
        return overrideHeight;
    }

    public boolean isClipToCircle() {
        return clipToCircle;
    }

    public boolean isCenterCrop() {
        return centerCrop;
    }

    public OnLoadImageListener getOnLoadImageListener() {
        return onLoadImageListener;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Object getLoadUrl() {
        return loadUrl;
    }

    @UmePriority
    public int getPriority() {
        return priority;
    }

    public boolean isDontAnimation() {
        return dontAnimation;
    }

    public int[] getRounds() {
        return rounds;
    }

    public boolean isFitXY() {
        return fitXY;
    }
}
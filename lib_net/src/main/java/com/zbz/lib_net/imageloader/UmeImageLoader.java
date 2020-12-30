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
 * @description Ume图片加载
 */
public class UmeImageLoader {
    private static final String TAG = UmeImageLoader.class.getSimpleName();

    // 网络图片url
    private String imgUrl;
    // 本地图片path
    private File filePath;
    // 资源图片id
    private int resourceId;
    // 设置磁盘缓存策略：
    @DiskCacheMode
    private int diskCacheMode = DiskCacheMode.NOT_SET;
    // 是否跳过内存缓存：
    private boolean skipMemoryCache = false;
    // 加载中占位图：
    private int placeholder = R.drawable.img_loading_placeholder;
    // 加载失败占位图：
    private int errorPlaceholder = R.drawable.img_error_placeholder;
    // 指定图片宽：
    private int overrideWidth = Target.SIZE_ORIGINAL;
    // 指定图片高：
    private int overrideHeight = Target.SIZE_ORIGINAL;
    // 是否切成圆形：
    private boolean clipToCircle;
    // 圆角大小数组
    private int[] rounds;
    // 将drawable转为bitmap：
    private boolean asBitmap;
    // 返回GifDrawable  gif是本地资源文件的话需要设置，http/https的貌似不用设置
    private boolean asGif;
    // 是否centerCrop:
    private boolean centerCrop;
    // 是否fitXY:
    private boolean fitXY;
    // 是否禁用动画：
    private boolean dontAnimation;
    // 是否仅在缓存中获取图片
    private boolean onlyRetrieveFromCache;
    // 加载返回对应的Drawable或Bitmap：
    private OnLoadImageListener onLoadImageListener;
    // 加载监听：
    private SimpleLoadImageListener simpleLoadImageListener;
    // ImageView
    private ImageView imageView;
    // context
    private Context context;
    // Priority 加载优先级
    @UmePriority
    private int priority = UmePriority.NORMAL;
    //最后由图片库加载的url：
    private Object loadUrl;

    private UmeImageLoader() {
    }

    public static UmeImageLoader with(Context context) {
        UmeImageLoader umeImageLoader = new UmeImageLoader();
        umeImageLoader.setContext(context);
        return umeImageLoader;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public UmeImageLoader priority(@UmePriority int priority) {
        this.priority = priority;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public UmeImageLoader asGif(boolean asGif) {
        this.asGif = asGif;
        return this;
    }

    public UmeImageLoader listener(SimpleLoadImageListener simpleLoadImageListener) {
        this.simpleLoadImageListener = simpleLoadImageListener;
        return this;
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

    public UmeImageLoader load(String imgUrl) {
        this.imgUrl = imgUrl;
        this.loadUrl = imgUrl;
        return this;
    }

    public UmeImageLoader load(File filePath) {
        this.filePath = filePath;
        this.loadUrl = filePath;
        return this;
    }

    public UmeImageLoader load(int resourceId) {
        this.resourceId = resourceId;
        this.loadUrl = resourceId;
        return this;
    }

    public UmeImageLoader diskCacheMode(@DiskCacheMode int diskCacheMode) {
        this.diskCacheMode = diskCacheMode;
        return this;
    }

    public UmeImageLoader skipMemoryCache(boolean skipMemoryCache) {
        this.skipMemoryCache = skipMemoryCache;
        return this;
    }

    public UmeImageLoader dontAnimation() {
        this.dontAnimation = true;
        return this;
    }

    public UmeImageLoader placeholder(int placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public UmeImageLoader onlyRetrieveFromCache(boolean onlyRetrieveFromCache) {
        this.onlyRetrieveFromCache = onlyRetrieveFromCache;
        return this;
    }

    public boolean isOnlyRetrieveFromCache() {
        return onlyRetrieveFromCache;
    }

    public UmeImageLoader error(int errorPlaceholder) {
        this.errorPlaceholder = errorPlaceholder;
        return this;
    }

    public UmeImageLoader override(int overrideWidth, int overrideHeight) {
        if (overrideWidth > 0) {
            this.overrideWidth = overrideWidth;
        }
        if (overrideHeight > 0) {
            this.overrideHeight = overrideHeight;
        }
        return this;
    }

    public UmeImageLoader asBitmap() {
        // 注意这样设置后，如果是gif图则不会动
        this.asBitmap = true;
        return this;
    }

    public UmeImageLoader cropToCircle() {
        this.clipToCircle = true;
        return this;
    }

    public UmeImageLoader centerCrop() {
        this.centerCrop = true;
        return this;
    }

    public UmeImageLoader fitXY() {
        this.fitXY = true;
        return this;
    }

    //左上开始顺时针
    public UmeImageLoader round(int round) {
        rounds(new int[]{round, round, round, round});
        return this;
    }

    public UmeImageLoader rounds(int[] rounds) {
        this.rounds = rounds;
        return this;
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;
        // 直接加载到ImageView上
        GlideUtil.loadImageAsDrawable(this);
    }

    public void into(OnLoadImageListener onLoadImageListener) {
        this.onLoadImageListener = onLoadImageListener;
        // 需要返回Bitmap或Drawable
        if (asBitmap) {
            GlideUtil.loadImageAsBitmap(this);
        } else if (asGif) {
            GlideUtil.loadImageAsGif(this);
        } else {
            GlideUtil.loadImageAsDrawable(this);
        }
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
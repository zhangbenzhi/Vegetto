package com.zbz.lib_net.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.zbz.lib_net.imageloader.listener.OnLoadImageListener;
import com.zbz.lib_net.imageloader.listener.SimpleLoadImageListener;
import com.zbz.lib_net.imageloader.mode.DiskCacheMode;
import com.zbz.lib_net.imageloader.mode.UmePriority;

import java.io.File;

/**
 * @author 张本志
 * @date 2020/12/30 21:56
 * @description 贝吉特图片加载参数构造
 */
public class VegettoImage {

    private VegettoImage() {
    }

    /**
     * 加载返回File文件：
     */
    public static File getFile(Context context, String url) {
        try {
            return GlideUtil.getFile(context, url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 加载返回Bitmap:
     */
    public static Bitmap getBitmap(Context context, String url) {
        try {
            return GlideUtil.getImageBitMap(context, url);
        } catch (Exception e) {
            return null;
        }
    }

    public static VegettoImageManager with(Context context) {
        VegettoImageParams vegettoImageParams = new VegettoImageParams();
        vegettoImageParams.context = context;
        return new VegettoImageManager(vegettoImageParams);
    }

    public static class VegettoImageManager {
        private VegettoImageParams vegettoImageParams;

        private VegettoImageManager(VegettoImageParams vegettoImageParams) {
            this.vegettoImageParams = vegettoImageParams;
        }

        public VegettoImageBuilder load(String url) {
            vegettoImageParams.imgUrl = url;
            vegettoImageParams.loadUrl = url;
            return new VegettoImageBuilder(vegettoImageParams);
        }

        public VegettoImageBuilder load(File filePath) {
            vegettoImageParams.filePath = filePath;
            vegettoImageParams.loadUrl = filePath;
            return new VegettoImageBuilder(vegettoImageParams);
        }

        public VegettoImageBuilder load(int resourceId) {
            vegettoImageParams.resourceId = resourceId;
            vegettoImageParams.loadUrl = resourceId;
            return new VegettoImageBuilder(vegettoImageParams);
        }
    }

    public static class VegettoImageBuilder {
        private VegettoImageParams vegettoImageParams;

        private VegettoImageBuilder(VegettoImageParams vegettoImageParams) {
            this.vegettoImageParams = vegettoImageParams;
        }

        public VegettoImageBuilder priority(@UmePriority int priority) {
            vegettoImageParams.priority = priority;
            return this;
        }

        public VegettoImageBuilder asGif(boolean asGif) {
            vegettoImageParams.asGif = asGif;
            return this;
        }

        public VegettoImageBuilder listener(SimpleLoadImageListener simpleLoadImageListener) {
            vegettoImageParams.simpleLoadImageListener = simpleLoadImageListener;
            return this;
        }

        public VegettoImageBuilder diskCacheMode(@DiskCacheMode int diskCacheMode) {
            vegettoImageParams.diskCacheMode = diskCacheMode;
            return this;
        }

        public VegettoImageBuilder skipMemoryCache(boolean skipMemoryCache) {
            vegettoImageParams.skipMemoryCache = skipMemoryCache;
            return this;
        }

        public VegettoImageBuilder dontAnimation() {
            vegettoImageParams.dontAnimation = true;
            return this;
        }

        public VegettoImageBuilder placeholder(int placeholder) {
            vegettoImageParams.placeholder = placeholder;
            return this;
        }

        public VegettoImageBuilder onlyRetrieveFromCache(boolean onlyRetrieveFromCache) {
            vegettoImageParams.onlyRetrieveFromCache = onlyRetrieveFromCache;
            return this;
        }

        public VegettoImageBuilder error(int errorPlaceholder) {
            vegettoImageParams.errorPlaceholder = errorPlaceholder;
            return this;
        }

        public VegettoImageBuilder override(int overrideWidth, int overrideHeight) {
            if (overrideWidth > 0) {
                vegettoImageParams.overrideWidth = overrideWidth;
            }
            if (overrideHeight > 0) {
                vegettoImageParams.overrideHeight = overrideHeight;
            }
            return this;
        }

        public VegettoImageBuilder asBitmap() {
            // 注意这样设置后，如果是gif图则不会动
            vegettoImageParams.asBitmap = true;
            return this;
        }

        public VegettoImageBuilder cropToCircle() {
            vegettoImageParams.clipToCircle = true;
            return this;
        }

        public VegettoImageBuilder centerCrop() {
            vegettoImageParams.centerCrop = true;
            return this;
        }

        public VegettoImageBuilder fitXY() {
            vegettoImageParams.fitXY = true;
            return this;
        }

        //左上开始顺时针
        public VegettoImageBuilder round(int round) {
            rounds(new int[]{round, round, round, round});
            return this;
        }

        public VegettoImageBuilder rounds(int[] rounds) {
            vegettoImageParams.rounds = rounds;
            return this;
        }

        public void into(ImageView imageView) {
            vegettoImageParams.imageView = imageView;
            // 直接加载到ImageView上
            GlideUtil.loadImageAsDrawable(vegettoImageParams);
        }

        public void into(OnLoadImageListener onLoadImageListener) {
            vegettoImageParams.onLoadImageListener = onLoadImageListener;
            // 需要返回Bitmap或Drawable
            if (vegettoImageParams.asBitmap) {
                GlideUtil.loadImageAsBitmap(vegettoImageParams);
            } else if (vegettoImageParams.asGif) {
                GlideUtil.loadImageAsGif(vegettoImageParams);
            } else {
                GlideUtil.loadImageAsDrawable(vegettoImageParams);
            }
        }
    }
}

package com.zbz.lib_net.imageloader.glide;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.zbz.lib_net.imageloader.mode.DiskCacheMode;
import com.zbz.lib_net.imageloader.UmeImageLoader;
import com.zbz.lib_net.imageloader.mode.UmePriority;
import com.zbz.lib_net.imageloader.transform.GlideFitXYTransform;
import com.zbz.lib_net.imageloader.transform.GlideRoundTransform;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author 张本志
 * @date 2020/7/16 09:54
 * @description Glide工具类
 */
public class GlideUtil {

    @SuppressLint("Ume_ImageLoadUse")
    public static Bitmap getImageBitMap(UmeImageLoader umeImageLoader) throws ExecutionException, InterruptedException {
        return Glide.with(umeImageLoader.getContext())
                .asBitmap()
                .load(umeImageLoader.getLoadUrl())
                .centerCrop()
                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();
    }

    @SuppressLint("Ume_ImageLoadUse")
    public static File getFile(UmeImageLoader umeImageLoader) throws ExecutionException, InterruptedException {
        return Glide.with(umeImageLoader.getContext())
                .downloadOnly()
                .load(umeImageLoader.getLoadUrl())
                .submit(umeImageLoader.getOverrideWidth(), umeImageLoader.getOverrideHeight())
                .get();
    }

    @SuppressLint("Ume_ImageLoadUse")
    public static void loadImageAsDrawable(final UmeImageLoader umeImageLoader) {
        if (umeImageLoader == null) {
            return;
        }
        RequestBuilder<Drawable> requestBuilder = Glide.with(umeImageLoader.getContext()).load(umeImageLoader.getLoadUrl());
        load(requestBuilder, umeImageLoader);
    }

    @SuppressLint("Ume_ImageLoadUse")
    public static void loadImageAsBitmap(final UmeImageLoader umeImageLoader) {
        if (umeImageLoader == null) {
            return;
        }
        RequestBuilder<Bitmap> requestBuilder = Glide.with(umeImageLoader.getContext()).asBitmap().load(umeImageLoader.getLoadUrl());
        load(requestBuilder, umeImageLoader);
    }

    @SuppressLint("Ume_ImageLoadUse")
    public static void loadImageAsGif(final UmeImageLoader umeImageLoader) {
        if (umeImageLoader == null) {
            return;
        }
        RequestBuilder<GifDrawable> requestBuilder = Glide.with(umeImageLoader.getContext()).asGif().load(umeImageLoader.getLoadUrl());
        load(requestBuilder, umeImageLoader);
    }


    @SuppressLint("CheckResult")
    private static void load(RequestBuilder requestBuilder, final UmeImageLoader umeImageLoader) {
        BaseRequestOptions baseRequestOptions = requestBuilder.load(umeImageLoader.getLoadUrl())
                .skipMemoryCache(umeImageLoader.isSkipMemoryCache())
                .onlyRetrieveFromCache(umeImageLoader.isOnlyRetrieveFromCache())
                .priority(mapToPriority(umeImageLoader.getPriority()));
        if (umeImageLoader.getDiskCacheMode() != DiskCacheMode.NOT_SET) {
            baseRequestOptions.diskCacheStrategy(mapToDiskCacheStrategy(umeImageLoader.getDiskCacheMode()));
        }
        if (umeImageLoader.getPlaceholder() != -1) {
            // 占位图：
            baseRequestOptions.placeholder(umeImageLoader.getPlaceholder());
        }
        if (umeImageLoader.getErrorPlaceholder() != -1) {
            // 失败图：
            baseRequestOptions.error(umeImageLoader.getErrorPlaceholder());
        }
        if (umeImageLoader.getOverrideWidth() != Target.SIZE_ORIGINAL
                && umeImageLoader.getOverrideHeight() != Target.SIZE_ORIGINAL) {
            baseRequestOptions.override(umeImageLoader.getOverrideWidth(), umeImageLoader.getOverrideHeight());
        }
        if (umeImageLoader.isDontAnimation()) {
            // 禁止动画，设置后gif也不会动了
            baseRequestOptions = baseRequestOptions.dontAnimate();
        }
        List<Transformation<Bitmap>> transformations = new ArrayList<>();
        if (umeImageLoader.isCenterCrop()) {
            // centerCrop:
            transformations.add(new CenterCrop());
        } else if (umeImageLoader.isFitXY()) {
            // fitXY:
            transformations.add(new GlideFitXYTransform());
        }
        if (umeImageLoader.getRounds() != null) {
            // 圆角：
            transformations.add(new GlideRoundTransform(umeImageLoader.getRounds()));
        } else if (umeImageLoader.isClipToCircle()) {
            // 圆：
            transformations.add(new CircleCrop());
        }
        if (transformations.size() > 0) {
            baseRequestOptions = baseRequestOptions.transform(new MultiTransformation<>(transformations));
        }
        if (baseRequestOptions instanceof RequestBuilder) {
            if (umeImageLoader.getSimpleLoadImageListener() != null) {
                baseRequestOptions = ((RequestBuilder) baseRequestOptions).listener(new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        return umeImageLoader.getSimpleLoadImageListener().onLoadFailed(e, isFirstResource);
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        return umeImageLoader.getSimpleLoadImageListener().onResourceReady(resource, isFirstResource);
                    }
                });
            }
            if (umeImageLoader.getOnLoadImageListener() != null) {
                ((RequestBuilder) baseRequestOptions).into(new CustomTarget<Object>() {
                    @Override
                    public void onResourceReady(@NonNull Object resource, @Nullable Transition<? super Object> transition) {
                        if (resource instanceof GifDrawable) {
                            ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                            ((GifDrawable) resource).start();
                        }
                        umeImageLoader.getOnLoadImageListener().onResourceReady(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        umeImageLoader.getOnLoadImageListener().onLoadFailed("加载图片失败");
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        umeImageLoader.getOnLoadImageListener().onLoadFailed("加载图片失败");
                    }
                });
            } else if (umeImageLoader.getImageView() != null) {
                requestBuilder.into(umeImageLoader.getImageView());
            }
        }
    }

    private static DiskCacheStrategy mapToDiskCacheStrategy(@DiskCacheMode int diskCacheMode) {
        DiskCacheStrategy diskCacheStrategy = DiskCacheStrategy.RESOURCE;
        switch (diskCacheMode) {
            case DiskCacheMode.ALL:
                diskCacheStrategy = DiskCacheStrategy.ALL;
                break;
            case DiskCacheMode.NONE:
                diskCacheStrategy = DiskCacheStrategy.NONE;
                break;
            case DiskCacheMode.SOURCE:
                diskCacheStrategy = DiskCacheStrategy.DATA;
                break;
            case DiskCacheMode.RESULT:
                diskCacheStrategy = DiskCacheStrategy.RESOURCE;
                break;
        }
        return diskCacheStrategy;
    }

    private static Priority mapToPriority(@UmePriority int umePriority) {
        Priority glidePriority = Priority.NORMAL;
        switch (umePriority) {
            case UmePriority.NORMAL:
                glidePriority = Priority.NORMAL;
                break;
            case UmePriority.IMMEDIATE:
                glidePriority = Priority.IMMEDIATE;
                break;
            case UmePriority.HIGH:
                glidePriority = Priority.HIGH;
                break;
            case UmePriority.LOW:
                glidePriority = Priority.LOW;
                break;
        }
        return glidePriority;
    }


}

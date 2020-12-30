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
import com.zbz.lib_net.imageloader.VegettoImageParams;
import com.zbz.lib_net.imageloader.mode.DiskCacheMode;
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
    public static Bitmap getImageBitMap(VegettoImageParams vegettoImageParams) throws ExecutionException, InterruptedException {
        return Glide.with(vegettoImageParams.getContext())
                .asBitmap()
                .load(vegettoImageParams.getLoadUrl())
                .centerCrop()
                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();
    }

    @SuppressLint("Ume_ImageLoadUse")
    public static File getFile(VegettoImageParams vegettoImageParams) throws ExecutionException, InterruptedException {
        return Glide.with(vegettoImageParams.getContext())
                .downloadOnly()
                .load(vegettoImageParams.getLoadUrl())
                .submit(vegettoImageParams.getOverrideWidth(), vegettoImageParams.getOverrideHeight())
                .get();
    }

    @SuppressLint("Ume_ImageLoadUse")
    public static void loadImageAsDrawable(final VegettoImageParams vegettoImageParams) {
        if (vegettoImageParams == null) {
            return;
        }
        RequestBuilder<Drawable> requestBuilder = Glide.with(vegettoImageParams.getContext()).load(vegettoImageParams.getLoadUrl());
        load(requestBuilder, vegettoImageParams);
    }

    @SuppressLint("Ume_ImageLoadUse")
    public static void loadImageAsBitmap(final VegettoImageParams vegettoImageParams) {
        if (vegettoImageParams == null) {
            return;
        }
        RequestBuilder<Bitmap> requestBuilder = Glide.with(vegettoImageParams.getContext()).asBitmap().load(vegettoImageParams.getLoadUrl());
        load(requestBuilder, vegettoImageParams);
    }

    @SuppressLint("Ume_ImageLoadUse")
    public static void loadImageAsGif(final VegettoImageParams vegettoImageParams) {
        if (vegettoImageParams == null) {
            return;
        }
        RequestBuilder<GifDrawable> requestBuilder = Glide.with(vegettoImageParams.getContext()).asGif().load(vegettoImageParams.getLoadUrl());
        load(requestBuilder, vegettoImageParams);
    }

    @SuppressLint("CheckResult")
    private static <T> void load(RequestBuilder requestBuilder, final VegettoImageParams vegettoImageParams) {
        BaseRequestOptions baseRequestOptions = requestBuilder.load(vegettoImageParams.getLoadUrl())
                .skipMemoryCache(vegettoImageParams.isSkipMemoryCache())
                .onlyRetrieveFromCache(vegettoImageParams.isOnlyRetrieveFromCache())
                .priority(mapToPriority(vegettoImageParams.getPriority()));
        if (vegettoImageParams.getDiskCacheMode() != DiskCacheMode.NOT_SET) {
            baseRequestOptions.diskCacheStrategy(mapToDiskCacheStrategy(vegettoImageParams.getDiskCacheMode()));
        }
        if (vegettoImageParams.getPlaceholder() != -1) {
            // 占位图：
            baseRequestOptions.placeholder(vegettoImageParams.getPlaceholder());
        }
        if (vegettoImageParams.getErrorPlaceholder() != -1) {
            // 失败图：
            baseRequestOptions.error(vegettoImageParams.getErrorPlaceholder());
        }
        if (vegettoImageParams.getOverrideWidth() != Target.SIZE_ORIGINAL
                && vegettoImageParams.getOverrideHeight() != Target.SIZE_ORIGINAL) {
            baseRequestOptions.override(vegettoImageParams.getOverrideWidth(), vegettoImageParams.getOverrideHeight());
        }
        if (vegettoImageParams.isDontAnimation()) {
            // 禁止动画，设置后gif也不会动了
            baseRequestOptions = baseRequestOptions.dontAnimate();
        }
        List<Transformation<Bitmap>> transformations = new ArrayList<>();
        if (vegettoImageParams.isCenterCrop()) {
            // centerCrop:
            transformations.add(new CenterCrop());
        } else if (vegettoImageParams.isFitXY()) {
            // fitXY:
            transformations.add(new GlideFitXYTransform());
        }
        if (vegettoImageParams.getRounds() != null) {
            // 圆角：
            transformations.add(new GlideRoundTransform(vegettoImageParams.getRounds()));
        } else if (vegettoImageParams.isClipToCircle()) {
            // 圆：
            transformations.add(new CircleCrop());
        }
        if (transformations.size() > 0) {
            baseRequestOptions = baseRequestOptions.transform(new MultiTransformation<>(transformations));
        }
        if (baseRequestOptions instanceof RequestBuilder) {
            if (vegettoImageParams.getSimpleLoadImageListener() != null) {
                ((RequestBuilder) baseRequestOptions).listener(new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        return vegettoImageParams.getSimpleLoadImageListener().onLoadFailed(e, isFirstResource);
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        return vegettoImageParams.getSimpleLoadImageListener().onResourceReady(resource, isFirstResource);
                    }
                });
            } else if (vegettoImageParams.getOnLoadImageListener() != null) {
                ((RequestBuilder) baseRequestOptions).into(new CustomTarget<Object>() {
                    @Override
                    public void onResourceReady(@NonNull Object resource, @Nullable Transition<? super Object> transition) {
                        if (resource instanceof GifDrawable) {
                            ((GifDrawable) resource).setLoopCount(GifDrawable.LOOP_FOREVER);
                            ((GifDrawable) resource).start();
                        }
                        vegettoImageParams.getOnLoadImageListener().onResourceReady(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        vegettoImageParams.getOnLoadImageListener().onLoadFailed("加载图片失败");
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        vegettoImageParams.getOnLoadImageListener().onLoadFailed("加载图片失败");
                    }
                });
            } else if (vegettoImageParams.getImageView() != null) {
                requestBuilder.into(vegettoImageParams.getImageView());
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
            case DiskCacheMode.NOT_SET:
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

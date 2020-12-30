package com.zbz.lib_net.imageloader.transform;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.PAINT_FLAGS;


public class GlideFitXYTransform extends BitmapTransformation {
    private static final String ID = GlideFitXYTransform.class.getSimpleName();
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    @Override
    protected Bitmap transform(
            @NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int width, int height) {
        if (inBitmap.getWidth() == width && inBitmap.getHeight() == height) {
            return inBitmap;
        }
        final float scaleX, scaleY;
        Matrix m = new Matrix();
        scaleX = (float) width / (float) inBitmap.getWidth();
        scaleY = (float) height / (float) inBitmap.getHeight();
        m.setScale(scaleX, scaleY);
        Bitmap result = pool.get(width, height, getNonNullConfig(inBitmap));
        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        TransformationUtils.setAlpha(inBitmap, result);
        applyMatrix(inBitmap, result, m);
        return result;
    }

    private static final Paint DEFAULT_PAINT = new Paint(PAINT_FLAGS);

    private synchronized static void applyMatrix(@NonNull Bitmap inBitmap, @NonNull Bitmap targetBitmap,
                                                 Matrix matrix) {
        Canvas canvas = new Canvas(targetBitmap);
        canvas.drawBitmap(inBitmap, matrix, DEFAULT_PAINT);
        clear(canvas);
    }

    // Avoids warnings in M+.
    private static void clear(Canvas canvas) {
        canvas.setBitmap(null);
    }

    @NonNull
    private static Bitmap.Config getNonNullConfig(@NonNull Bitmap bitmap) {
        return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GlideFitXYTransform;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}

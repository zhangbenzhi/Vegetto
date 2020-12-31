package com.zbz.lib_ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

/**
 * @author 张本志
 * @date 2020/12/28 11:51
 * @description 倒计时view
 */
public class CountDownView extends View {

    private Paint outCirclePaint;
    private Paint innerCirclePaint;
    private Paint textPaint;
    private int width;
    private int height;
    private int ringWith = 10;//圆环宽度
    private int angele;//当前角度
    private String text;
    private CountDownTimer countDownTimer;

    public CountDownView(Context context) {
        super(context);
        init();
    }

    public CountDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //禁止硬件加速，图层混合模式有些不支持
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // 外圆：
        outCirclePaint = new Paint();
        outCirclePaint.setAntiAlias(true);
        outCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.lib_ui_out_circle_color));
        // 内圆：
        innerCirclePaint = new Paint();
        innerCirclePaint.setAntiAlias(true);
        innerCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.lib_ui_innner_circle_color));
        // 文字：
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(8);
        textPaint.setTextSize(36);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.lib_ui_white));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画外圆：
        drawOutCircle(canvas);
        // 画内圆：
        drawInnerCircle(canvas);
        // 画文字：
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        RectF rectF = new RectF(0, 0, width, height);
        // 文字居中:https://www.jianshu.com/p/8b97627b21c4
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseLine = rectF.centerY() + distance;
        canvas.drawText(text, rectF.centerX(), baseLine, textPaint);
    }

    private void drawInnerCircle(Canvas canvas) {
        canvas.drawCircle(width >> 1, height >> 1, (width >> 1) - ringWith, innerCirclePaint);
    }

    private void drawOutCircle(Canvas canvas) {
        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawArc(rectF, 0, angele, true, outCirclePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        int defaultWidth = 150;
        if (wMode == MeasureSpec.EXACTLY) {
            width = wSize;
        } else {
            width = Math.min(defaultWidth, wSize);
        }
        if (hMode == MeasureSpec.EXACTLY) {
            height = hSize;
        } else {
            height = Math.min(defaultWidth, hSize);
        }
        setMeasuredDimension(width, height);
    }

    /**
     * 开始倒计时：
     *
     * @param milliSeconds        总计多少毫秒
     * @param onCountDownListener 监听
     */
    public void beginCountDown(final long milliSeconds, final OnCountDownListener onCountDownListener) {
        if (milliSeconds <= 0) {
            return;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        angele = 0;
        text = String.valueOf(milliSeconds / 1000);
        countDownTimer = new CountDownTimer(milliSeconds, milliSeconds / 200) {
            @Override
            public void onTick(long millisUntilFinished) {
                angele = (int) ((float) (milliSeconds - millisUntilFinished) / milliSeconds * 360);
                text = String.valueOf(millisUntilFinished / 1000 + 1);
                invalidate();
            }

            @Override
            public void onFinish() {
                angele = 360;
                text = "0";
                invalidate();
                onCountDownListener.onFinish();
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public interface OnCountDownListener {
        void onFinish();
    }
}

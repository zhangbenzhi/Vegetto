package com.zbz.lib_player;

import android.content.Context;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 控制器抽象类
 */
public abstract class NiceVideoPlayerController
        extends FrameLayout {

    private Context mContext;
    protected INiceVideoPlayer mNiceVideoPlayer;

    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    private float mDownX;
    private float mDownY;
    private boolean mNeedChangePosition;
    private boolean mNeedChangeVolume;
    private boolean mNeedChangeBrightness;
    private static final int THRESHOLD = 80;
    private long mGestureDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mNewPosition;
    private boolean vslideGesture = false;// 非全屏下是否开启手势
    private boolean vslideGestureFullscreen = true;// 全屏模式下是否开启手势
    protected boolean playGesture;//在非全屏模式下，是否开启播放手势，即双击切换播放/暂停
    protected boolean playGestureFullscreen = true;//在全屏模式下，是否开启播放手势，即双击切换播放/暂停
    protected boolean isLocked;//是否锁屏了
    protected OnSingleDoubleClickListener onSingleDoubleClickListener;

    public NiceVideoPlayerController(Context context) {
        super(context);
        mContext = context;
    }

    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        mNiceVideoPlayer = niceVideoPlayer;
    }

    /**
     * 设置播放的视频的标题
     *
     * @param title 视频标题
     */
    public abstract void setTitle(String title);

    /**
     * 视频底图
     *
     * @param resId 视频底图资源
     */
    public abstract void setImage(@DrawableRes int resId);

    /**
     * 视频底图ImageView控件，提供给外部用图片加载工具来加载网络图片
     *
     * @return 底图ImageView
     */
    public abstract ImageView imageView();

    /**
     * 设置总时长.
     */
    public abstract void setLenght(long length);

    /**
     * 当播放器的播放状态发生变化，在此方法中国你更新不同的播放状态的UI
     *
     * @param playState 播放状态：
     *                  <ul>
     *                  <li>{@link NiceVideoPlayer#STATE_IDLE}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PREPARING}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PREPARED}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PLAYING}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_PAUSED}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_BUFFERING_PLAYING}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_BUFFERING_PAUSED}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_ERROR}</li>
     *                  <li>{@link NiceVideoPlayer#STATE_COMPLETED}</li>
     *                  </ul>
     */
    protected abstract void onPlayStateChanged(int playState);

    /**
     * 当播放器的播放模式发生变化，在此方法中更新不同模式下的控制器界面。
     *
     * @param playMode 播放器的模式：
     *                 <ul>
     *                 <li>{@link NiceVideoPlayer#MODE_NORMAL}</li>
     *                 <li>{@link NiceVideoPlayer#MODE_FULL_SCREEN}</li>
     *                 <li>{@link NiceVideoPlayer#MODE_TINY_WINDOW}</li>
     *                 </ul>
     */
    protected abstract void onPlayModeChanged(int playMode);

    /**
     * 重置控制器，将控制器恢复到初始状态。
     */
    protected abstract void reset();

    /**
     * 开启更新进度的计时器。
     */
    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    NiceVideoPlayerController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    /**
     * 取消更新进度的计时器。
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    /**
     * 更新进度，包括进度条进度，展示的当前播放位置时长，总时长等。
     */
    protected abstract void updateProgress();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                mNeedChangeBrightness = false;
                if (onSingleDoubleClickListener != null) {
                    if (clickRunnable == null) {
                        clickRunnable = new Runnable() {
                            @Override
                            public void run() {
                                // 超过双击时间，表示为单击：
                                onSingleDoubleClickListener.onSingleClick();
                                NiceVideoPlayerController.this.removeCallbacks(clickRunnable);
                                clickRunnable = null;
                            }
                        };
                        NiceVideoPlayerController.this.postDelayed(clickRunnable, 300);
                    } else {
                        // 在双击时间范围内，表示为双击
                        onSingleDoubleClickListener.onDoubleClick();
                        NiceVideoPlayerController.this.removeCallbacks(clickRunnable);
                        clickRunnable = null;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (handleOnTouch()) {
                    return false;
                }
                float deltaX = x - mDownX;
                float deltaY = y - mDownY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if (!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness) {
                    // 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
                    if (absDeltaX >= THRESHOLD) {
                        cancelUpdateProgressTimer();
                        mNeedChangePosition = true;
                        mGestureDownPosition = mNiceVideoPlayer.getCurrentPosition();
                    } else if (absDeltaY >= THRESHOLD) {
                        if (mDownX < getWidth() * 0.5f) {
                            // 左侧改变亮度
                            mNeedChangeBrightness = true;
                            mGestureDownBrightness = NiceUtil.scanForActivity(mContext)
                                    .getWindow().getAttributes().screenBrightness;
                        } else {
                            // 右侧改变声音
                            mNeedChangeVolume = true;
                            mGestureDownVolume = mNiceVideoPlayer.getVolume();
                        }
                    }
                }
                if (mNeedChangePosition) {
                    long duration = mNiceVideoPlayer.getDuration();
                    long toPosition = (long) (mGestureDownPosition + duration * deltaX / getWidth());
                    mNewPosition = Math.max(0, Math.min(duration, toPosition));
                    int newPositionProgress = (int) (100f * mNewPosition / duration);
                    showChangePosition(duration, newPositionProgress);
                }
                if (mNeedChangeBrightness) {
                    deltaY = -deltaY;
                    float deltaBrightness = deltaY * 3 / getHeight();
                    float newBrightness = mGestureDownBrightness + deltaBrightness;
                    newBrightness = Math.max(0, Math.min(newBrightness, 1));
                    float newBrightnessPercentage = newBrightness;
                    WindowManager.LayoutParams params = NiceUtil.scanForActivity(mContext)
                            .getWindow().getAttributes();
                    params.screenBrightness = newBrightnessPercentage;
                    NiceUtil.scanForActivity(mContext).getWindow().setAttributes(params);
                    int newBrightnessProgress = (int) (100f * newBrightnessPercentage);
                    showChangeBrightness(newBrightnessProgress);
                }
                if (mNeedChangeVolume) {
                    deltaY = -deltaY;
                    int maxVolume = mNiceVideoPlayer.getMaxVolume();
                    int deltaVolume = (int) (maxVolume * deltaY * 3 / getHeight());
                    int newVolume = mGestureDownVolume + deltaVolume;
                    newVolume = Math.max(0, Math.min(maxVolume, newVolume));
                    mNiceVideoPlayer.setVolume(newVolume);
                    int newVolumeProgress = (int) (100f * newVolume / maxVolume);
                    showChangeVolume(newVolumeProgress);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (handleOnTouch()) {
                    return false;
                }
                if (mNeedChangePosition) {
                    mNiceVideoPlayer.seekTo(mNewPosition);
                    hideChangePosition();
                    startUpdateProgressTimer();
                    return true;
                }
                if (mNeedChangeBrightness) {
                    hideChangeBrightness();
                    return true;
                }
                if (mNeedChangeVolume) {
                    hideChangeVolume();
                    return true;
                }
                break;
        }
        return false;
    }

    private Runnable clickRunnable;

    private boolean handleOnTouch() {
        // 只有在播放、暂停、缓冲的时候能够拖动改变位置、亮度和声音
        if (isLocked) {
            // 锁屏了。。。。
            return true;
        }
        if (!mNiceVideoPlayer.isFullScreen() && !vslideGesture) {
            // 非全屏下未开启进度、亮度和声音的手势：
            return true;
        }
        if (mNiceVideoPlayer.isFullScreen() && !vslideGestureFullscreen) {
            // 全屏下未开启进度、亮度和声音的手势：
            return true;
        }
        if (mNiceVideoPlayer.isIdle()
                || mNiceVideoPlayer.isError()
                || mNiceVideoPlayer.isPreparing()
                || mNiceVideoPlayer.isPrepared()
                || mNiceVideoPlayer.isCompleted()) {
            hideChangePosition();
            hideChangeBrightness();
            hideChangeVolume();
            return true;
        }
        return false;
    }

    /**
     * 手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param duration            视频总时长ms
     * @param newPositionProgress 新的位置进度，取值0到100。
     */
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    /**
     * 手势左右滑动改变播放位置后，手势up或者cancel时，隐藏控制器中间的播放位置变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangePosition();

    /**
     * 手势在右侧上下滑动改变音量时，显示控制器中间的音量变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newVolumeProgress 新的音量进度，取值1到100。
     */
    protected abstract void showChangeVolume(int newVolumeProgress);

    protected abstract void onVolumeChange(int newVolumeProgress);

    /**
     * 手势在左侧上下滑动改变音量后，手势up或者cancel时，隐藏控制器中间的音量变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeVolume();

    /**
     * 手势在左侧上下滑动改变亮度时，显示控制器中间的亮度变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param newBrightnessProgress 新的亮度进度，取值1到100。
     */
    protected abstract void showChangeBrightness(int newBrightnessProgress);

    /**
     * 手势在左侧上下滑动改变亮度后，手势up或者cancel时，隐藏控制器中间的亮度变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    protected abstract void hideChangeBrightness();

    protected abstract void onGetVideoInfo(int width, int height, long duration);

    protected abstract void progress(float percent);

    public void setVslideGesture(boolean vslideGesture) {
        this.vslideGesture = vslideGesture;
    }

    public void setVslideGestureFullscreen(boolean vslideGestureFullscreen) {
        this.vslideGestureFullscreen = vslideGestureFullscreen;
    }

    public void setPlayGesture(boolean playGesture) {
        this.playGesture = playGesture;
    }

    public void setPlayGestureFullscreen(boolean playGestureFullscreen) {
        this.playGestureFullscreen = playGestureFullscreen;
    }

    public interface OnSingleDoubleClickListener {
        void onSingleClick();

        void onDoubleClick();
    }
}

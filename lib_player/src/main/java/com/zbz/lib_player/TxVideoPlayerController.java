package com.zbz.lib_player;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.zbz.lib_net.imageloader.VegettoImage;
import com.zbz.lib_net.imageloader.listener.OnLoadImageListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.zbz.lib_player.NiceVideoPlayer.STATE_PLAYING;


/**
 * 仿腾讯视频热点列表页播放器控制器.
 */
public class TxVideoPlayerController
        extends NiceVideoPlayerController
        implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        ChangeClarityDialog.OnClarityChangedListener {

    private Context mContext;
    private ImageView mImage;
    private ImageView mCenterStart;
    private LinearLayout mTop;
    private ImageView mBack;
    private TextView mTitle;
    private LinearLayout mBatteryTime;
    private TextView mTime;
    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private TextView mClarity;
    private ImageView mFullScreen;
    private LinearLayout mLoading;
    private TextView mLoadText;
    private LinearLayout mChangePositon;
    private TextView mChangePositionCurrent;
    private ProgressBar mChangePositionProgress;
    private LinearLayout mChangeBrightness;
    private ProgressBar mChangeBrightnessProgress;
    private LinearLayout mChangeVolume;
    private ProgressBar mChangeVolumeProgress;
    private LinearLayout mError;
    private TextView mRetry;
    private LinearLayout mCompleted;
    private TextView mReplay;
    private boolean topBottomVisible;
    private CountDownTimer mDismissTopBottomCountDownTimer;
    private List<Clarity> clarities;
    private int defaultClarityIndex;
    private ChangeClarityDialog mClarityDialog;
    private NicePlayerListener nicePlayerListener;
    private ImageView mVolumeIv;
    private boolean mutedMode;
    private ImageView lockIv;
    private boolean weexMakeShowLock = true;
    private int preVolume;
    private boolean showControls = true;//非全屏下是否显示控制面板
    private String fitMode = NiceVideoMode.FIT_CONTAIN;
    private View mControllerView;

    public TxVideoPlayerController(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.tx_video_palyer_controller, this, true);

        mCenterStart = findViewById(R.id.center_start);
        mImage = findViewById(R.id.image_bg);

        mTop = findViewById(R.id.top);
        mBack = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mBatteryTime = findViewById(R.id.battery_time);
        mTime = findViewById(R.id.time);
        lockIv = findViewById(R.id.lock_iv);
        mBottom = findViewById(R.id.bottom);
        mRestartPause = findViewById(R.id.restart_or_pause);
        mPosition = findViewById(R.id.position);
        mDuration = findViewById(R.id.duration);
        mSeek = findViewById(R.id.seek);
        mFullScreen = findViewById(R.id.full_screen);
        mClarity = findViewById(R.id.clarity);
        mControllerView = findViewById(R.id.rl_controller);

        mLoading = findViewById(R.id.loading);
        mLoadText = findViewById(R.id.load_text);

        mChangePositon = findViewById(R.id.change_position);
        mChangePositionCurrent = findViewById(R.id.change_position_current);
        mChangePositionProgress = findViewById(R.id.change_position_progress);

        mChangeBrightness = findViewById(R.id.change_brightness);
        mChangeBrightnessProgress = findViewById(R.id.change_brightness_progress);

        mChangeVolume = findViewById(R.id.change_volume);
        mChangeVolumeProgress = findViewById(R.id.change_volume_progress);

        mError = findViewById(R.id.error);
        mRetry = findViewById(R.id.retry);

        mCompleted = findViewById(R.id.completed);
        mReplay = findViewById(R.id.replay);
        mVolumeIv = findViewById(R.id.iv_video_volume);
        mVolumeIv.setOnClickListener(this);

        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mClarity.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        lockIv.setOnClickListener(this);
        findViewById(R.id.completed).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.onSingleDoubleClickListener = new OnSingleDoubleClickListener() {
            @Override
            public void onSingleClick() {
                if (nicePlayerListener != null) {
                    nicePlayerListener.onClickEvent(mNiceVideoPlayer.isPlaying(), mNiceVideoPlayer.isFullScreen(), mNiceVideoPlayer.getCurrentPosition() / 1000f);
                }
                if (mNiceVideoPlayer != null) {
                    if (mNiceVideoPlayer.isPlaying()
                            || mNiceVideoPlayer.isPaused()
                            || mNiceVideoPlayer.isBufferingPlaying()
                            || mNiceVideoPlayer.isBufferingPaused()) {
                        setTopBottomVisible(!topBottomVisible);
                        setLockIvVisible(lockIv.getVisibility() == View.GONE);
                    }
                }
            }

            @Override
            public void onDoubleClick() {
                if (nicePlayerListener != null) {
                    nicePlayerListener.onDoubleClick(mNiceVideoPlayer.isPlaying(), mNiceVideoPlayer.isFullScreen(), mNiceVideoPlayer.getCurrentPosition() / 1000f);
                }
                if (isLocked) {
                    // 锁屏状态：
                    return;
                }
                // play or pause
                if (mNiceVideoPlayer.isFullScreen() && playGestureFullscreen) {
                    // 全屏下使用双击手势:
                    if (mRestartPause != null) {
                        mRestartPause.performClick();
                    }
                } else if (!mNiceVideoPlayer.isFullScreen() && playGesture) {
                    // 非全屏下使用双击手势:
                    if (mRestartPause != null) {
                        mRestartPause.performClick();
                    }
                }
            }
        };
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public ImageView imageView() {
        return mImage;
    }

    @Override
    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }

    @Override
    public void setLenght(long length) {
        mDuration.setText(NiceUtil.formatTime(length));
    }

    @Override
    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        super.setNiceVideoPlayer(niceVideoPlayer);
        // 给播放器配置视频链接地址
        if (clarities != null && clarities.size() > 1) {
            mNiceVideoPlayer.setUp(clarities.get(defaultClarityIndex).videoUrl, null);
        }
    }

    /**
     * 设置清晰度
     */
    public void setClarity(List<Clarity> clarities, int defaultClarityIndex) {
        if (clarities != null && clarities.size() > 1) {
            this.clarities = clarities;
            this.defaultClarityIndex = defaultClarityIndex;

            List<String> clarityGrades = new ArrayList<>();
            for (Clarity clarity : clarities) {
                clarityGrades.add(clarity.grade + " " + clarity.p);
            }
            mClarity.setText(clarities.get(defaultClarityIndex).grade);
            // 初始化切换清晰度对话框
            mClarityDialog = new ChangeClarityDialog(mContext);
            mClarityDialog.setClarityGrade(clarityGrades, defaultClarityIndex);
            mClarityDialog.setOnClarityCheckedListener(this);
            // 给播放器配置视频链接地址
            if (mNiceVideoPlayer != null) {
                mNiceVideoPlayer.setUp(clarities.get(defaultClarityIndex).videoUrl, null);
            }
        }
    }

    @Override
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case NiceVideoPlayer.STATE_IDLE:
                break;
            case NiceVideoPlayer.STATE_PREPARING:
                mLoading.setVisibility(View.VISIBLE);
                mLoadText.setText("正在准备...");
                mError.setVisibility(View.GONE);
                mCompleted.setVisibility(View.GONE);
                mTop.setVisibility(View.GONE);
                mBottom.setVisibility(View.GONE);
                mCenterStart.setVisibility(View.GONE);
                break;
            case NiceVideoPlayer.STATE_PREPARED:
                startUpdateProgressTimer();
                if (nicePlayerListener != null) {
                    nicePlayerListener.onPrepared();
                }
                break;
            case STATE_PLAYING:
                mLoading.setVisibility(View.GONE);
                mImage.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.player_pause);
                startDismissTopBottomTimer();
                if (nicePlayerListener != null) {
                    nicePlayerListener.onStart();
                }
                break;
            case NiceVideoPlayer.STATE_PAUSED:
                mLoading.setVisibility(View.GONE);
                mRestartPause.setImageResource(R.drawable.player_start);
                cancelDismissTopBottomTimer();
                if (nicePlayerListener != null) {
                    nicePlayerListener.onPause();
                }
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PLAYING:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.player_pause);
                mLoadText.setText("正在缓冲...");
                startDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_BUFFERING_PAUSED:
                mLoading.setVisibility(View.VISIBLE);
                mRestartPause.setImageResource(R.drawable.player_start);
                mLoadText.setText("正在缓冲...");
                cancelDismissTopBottomTimer();
                break;
            case NiceVideoPlayer.STATE_ERROR:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mTop.setVisibility(View.VISIBLE);
                mError.setVisibility(View.VISIBLE);
                if (nicePlayerListener != null) {
                    nicePlayerListener.onError("加载失败");
                }
                break;
            case NiceVideoPlayer.STATE_COMPLETED:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                setTopVisible(true);
                setLockIvVisible(false);
                mImage.setVisibility(View.VISIBLE);
                mCompleted.setVisibility(View.VISIBLE);
                if (nicePlayerListener != null) {
                    nicePlayerListener.onCompletion();
                }
                break;
        }
    }

    @Override
    protected void onPlayModeChanged(int playMode) {
        this.playMode = playMode;
        switch (playMode) {
            case NiceVideoPlayer.MODE_NORMAL:
                mBack.setVisibility(View.GONE);
                mFullScreen.setImageResource(R.drawable.video_full_screen);
                mFullScreen.setVisibility(View.VISIBLE);
                mClarity.setVisibility(View.GONE);
                mBatteryTime.setVisibility(View.GONE);
                if (nicePlayerListener != null) {
                    nicePlayerListener.onExitFull(true);
                    mTitle.setVisibility(View.GONE);
                }
                lockIv.setVisibility(View.GONE);
                isLocked = false;
                lockIv.setImageResource(R.drawable.ic_nice_player_unlock);
                // 非全屏：
                mControllerView.setVisibility(showControls ? View.VISIBLE : View.GONE);
                break;
            case NiceVideoPlayer.MODE_FULL_SCREEN:
                mBack.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.GONE);
                mFullScreen.setImageResource(R.drawable.video_full_screen);
                if (clarities != null && clarities.size() > 1) {
                    mClarity.setVisibility(View.VISIBLE);
                }
                mBatteryTime.setVisibility(View.VISIBLE);
                if (nicePlayerListener != null) {
                    nicePlayerListener.onFullScreen(false);
                    mTitle.setVisibility(View.VISIBLE);
                }
                setLockIvVisible(weexMakeShowLock);
                // 全屏：
                mControllerView.setVisibility(View.VISIBLE);
                break;
            case NiceVideoPlayer.MODE_TINY_WINDOW:
                mBack.setVisibility(View.VISIBLE);
                mClarity.setVisibility(View.GONE);
                lockIv.setVisibility(View.GONE);
                break;
        }
    }

    private String mImgUrl;

    public void setBgImg(final String url) {
        if (mImgUrl != null && !mImgUrl.equals(url) && mImage != null) {
            // 封面图变了,先清一下
            mImage.setImageDrawable(null);
        }
        if (!TextUtils.isEmpty(url) && mImage != null) {
            mImage.setTag(R.id.video_player_img_tag, url);
            fitMode = fitMode == null ? "" : fitMode;
            switch (fitMode) {
                case NiceVideoMode.FIT_FILL:
                    // 拉伸
                    mImage.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case NiceVideoMode.FIT_COVER:
                    // 保持比例：
                    mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                default:
                    mImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    break;
            }
            mImgUrl = url;
            VegettoImage.with(getContext()).load(url).error(-1).placeholder(-1).into(new OnLoadImageListener<Drawable>() {
                @Override
                public void onResourceReady(Drawable drawable) {
                    if (mImage != null) {
                        if (url.equals(mImage.getTag(R.id.video_player_img_tag))) {
                            mImage.setImageDrawable(drawable);
                            if (nicePlayerListener != null) {
                                nicePlayerListener.onPosterLoaded(true);
                            }
                        }
                    }
                }

                @Override
                public void onLoadFailed(String msg) {
                    super.onLoadFailed(msg);
                    if (mImage != null) {
                        if (url.equals(mImage.getTag(R.id.video_player_img_tag))) {
                            if (nicePlayerListener != null) {
                                nicePlayerListener.onPosterLoaded(false);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void reset() {
        topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);

        mCenterStart.setVisibility(View.VISIBLE);
        mImage.setVisibility(View.VISIBLE);

        mBottom.setVisibility(View.GONE);
        mFullScreen.setImageResource(R.drawable.video_full_screen);

        mDuration.setVisibility(View.VISIBLE);

        mTop.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.GONE);

        mLoading.setVisibility(View.GONE);
        mError.setVisibility(View.GONE);
        mCompleted.setVisibility(View.GONE);
    }

    /**
     * 尽量不要在onClick中直接处理控件的隐藏、显示及各种UI逻辑。
     * UI相关的逻辑都尽量到{@link #onPlayStateChanged}和{@link #onPlayModeChanged}中处理.
     */
    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
            if (mNiceVideoPlayer.isIdle()) {
                mNiceVideoPlayer.start();
            }
        } else if (v == mBack) {
            if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            } else if (mNiceVideoPlayer.isTinyWindow()) {
                mNiceVideoPlayer.exitTinyWindow();
            }
        } else if (v == mRestartPause) {
            if (mNiceVideoPlayer.isPlaying() || mNiceVideoPlayer.isBufferingPlaying()) {
                mNiceVideoPlayer.pause();
            } else if (mNiceVideoPlayer.isPaused() || mNiceVideoPlayer.isBufferingPaused()) {
                mNiceVideoPlayer.restart();
            }
        } else if (v == mFullScreen) {
            if (mNiceVideoPlayer.isNormal() || mNiceVideoPlayer.isTinyWindow()) {
                mNiceVideoPlayer.enterFullScreen();
            } else if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            }
        } else if (v == mClarity) {
            setTopBottomVisible(false); // 隐藏top、bottom
            mClarityDialog.show();     // 显示清晰度对话框
        } else if (v == mRetry) {
            mNiceVideoPlayer.restart();
        } else if (v == mReplay) {
            mRetry.performClick();
        } else if (v == mVolumeIv) {
            // 设置静音或者音量：
            if (mNiceVideoPlayer != null) {
                if (!mutedMode) {
                    // 当前为非静音模式,先记录下当前音量
                    preVolume = mNiceVideoPlayer.getVolume();
                    mNiceVideoPlayer.setVolume(0);
                    onVolumeChange(0);
                } else {
                    // 当前为静音模式，设置为记录的音量
                    preVolume = preVolume <= 0 ? 3 : preVolume;
                    mNiceVideoPlayer.setVolume(preVolume);
                    onVolumeChange(preVolume);
                }
            }
        } else if (v == lockIv) {
            isLocked = !isLocked;
            lockIv.setImageResource(isLocked ? R.drawable.ic_nice_player_lock : R.drawable.ic_nice_player_unlock);
            setTopBottomVisible(!isLocked);
        }
    }

    @Override
    public void onClarityChanged(int clarityIndex) {
        // 根据切换后的清晰度索引值，设置对应的视频链接地址，并从当前播放位置接着播放
        Clarity clarity = clarities.get(clarityIndex);
        mClarity.setText(clarity.grade);
        long currentPosition = mNiceVideoPlayer.getCurrentPosition();
        mNiceVideoPlayer.releasePlayer();
        mNiceVideoPlayer.setUp(clarity.videoUrl, null);
        mNiceVideoPlayer.start(currentPosition);
    }

    @Override
    public void onClarityNotChanged() {
        // 清晰度没有变化，对话框消失后，需要重新显示出top、bottom
        setTopBottomVisible(true);
    }

    /**
     * 设置top、bottom的显示和隐藏
     *
     * @param visible true显示，false隐藏.
     */
    private void setTopBottomVisible(boolean visible) {
        if (isLocked) {
            setTopVisible(false);
            setBottomVisible(false);
            topBottomVisible = visible;
            cancelDismissTopBottomTimer();
            return;
        }
        setTopVisible(visible);
        setBottomVisible(visible);
        topBottomVisible = visible;
        if (visible) {
            if (!mNiceVideoPlayer.isPaused() && !mNiceVideoPlayer.isBufferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }

    private void setLockIvVisible(boolean show) {
        lockIv.setVisibility(View.GONE);
        if (show && weexMakeShowLock && playMode == NiceVideoPlayer.MODE_FULL_SCREEN) {
            lockIv.setVisibility(View.VISIBLE);
        }
    }

    private int playMode;

    public void weexMakeShowLock(boolean weexMakeShowLock) {
        this.weexMakeShowLock = weexMakeShowLock;
        setLockIvVisible(weexMakeShowLock);
    }


    private void setTopVisible(boolean visible) {
        mTop.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setBottomVisible(boolean visible) {
        mBottom.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 开启top、bottom自动消失的timer
     */
    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                    setLockIvVisible(false);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();
    }

    /**
     * 取消top、bottom自动消失的timer
     */
    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mNiceVideoPlayer.isBufferingPaused() || mNiceVideoPlayer.isPaused()) {
            mNiceVideoPlayer.restart();
        }
        long position = (long) (mNiceVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
        mNiceVideoPlayer.seekTo(position);
        startDismissTopBottomTimer();
    }

    @Override
    protected void updateProgress() {
        long position = mNiceVideoPlayer.getCurrentPosition();
        long duration = mNiceVideoPlayer.getDuration();
        int bufferPercentage = mNiceVideoPlayer.getBufferPercentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(NiceUtil.formatTime(position));
        mDuration.setText(NiceUtil.formatTime(duration));
        // 更新时间
        mTime.setText(new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date()));
        if (nicePlayerListener != null && mNiceVideoPlayer.getCurrentState() == STATE_PLAYING) {
            // 播放状态下才回调：
            nicePlayerListener.onProgressChanged(position / 1000f, duration / 1000f);
        }
    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {
        mChangePositon.setVisibility(View.VISIBLE);
        long newPosition = (long) (duration * newPositionProgress / 100f);
        mChangePositionCurrent.setText(NiceUtil.formatTime(newPosition));
        mChangePositionProgress.setProgress(newPositionProgress);
        mSeek.setProgress(newPositionProgress);
        mPosition.setText(NiceUtil.formatTime(newPosition));
    }

    @Override
    protected void hideChangePosition() {
        mChangePositon.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeVolume(int newVolumeProgress) {
        mChangeVolume.setVisibility(View.VISIBLE);
        mChangeVolumeProgress.setProgress(newVolumeProgress);
        onVolumeChange(newVolumeProgress);
    }

    @Override
    protected void onVolumeChange(int newVolumeProgress) {
        if (newVolumeProgress == 0) {
            mVolumeIv.setImageResource(R.drawable.video_volume_mute);
            mutedMode = true;
        } else {
            mVolumeIv.setImageResource(R.drawable.video_volume_change);
            mutedMode = false;
        }
    }

    @Override
    protected void hideChangeVolume() {
        mChangeVolume.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeBrightness(int newBrightnessProgress) {
        mChangeBrightness.setVisibility(View.VISIBLE);
        mChangeBrightnessProgress.setProgress(newBrightnessProgress);
    }

    @Override
    protected void hideChangeBrightness() {
        mChangeBrightness.setVisibility(View.GONE);
    }

    @Override
    protected void onGetVideoInfo(int width, int height, long duration) {
        if (nicePlayerListener != null) {
            nicePlayerListener.onGetVideoInfo(width, height, duration);
        }
    }

    @Override
    protected void progress(float percent) {
        if (nicePlayerListener != null) {
            nicePlayerListener.progress(percent);
        }
    }

    public void setNicePlayerListener(NicePlayerListener nicePlayerListener) {
        this.nicePlayerListener = nicePlayerListener;
    }

    public void performFullScreen(boolean fullScreen) {
        if (mNiceVideoPlayer == null) {
            return;
        }
        if (fullScreen) {
            if (mNiceVideoPlayer.isNormal() || mNiceVideoPlayer.isTinyWindow()) {
                mNiceVideoPlayer.enterFullScreen();
            }
        } else {
            if (mNiceVideoPlayer.isFullScreen()) {
                mNiceVideoPlayer.exitFullScreen();
            }
        }
    }

    public void performVolumeClick() {
        if (mVolumeIv != null) {
            mVolumeIv.performClick();
        }
    }

    public void setShowControls(boolean showControls) {
        this.showControls = showControls;
        if (showControls) {
            if (mControllerView != null) {
                mControllerView.setVisibility(View.VISIBLE);
            }
        } else if (mNiceVideoPlayer != null && !mNiceVideoPlayer.isFullScreen()) {
            // 全屏下一直显示控制面板，非全屏下由weex控制
            if (mControllerView != null) {
                mControllerView.setVisibility(View.GONE);
            }
        }
    }

    public void setVideoFitMode(String fitMode) {
        this.fitMode = fitMode;
    }

    //播放器对外暴露的接口
    public interface NicePlayerListener {
        void onError(String msg);

        void onPrepared();

        void onCompletion();

        void onPause();

        void onStart();

        void onFullScreen(boolean port);

        void onExitFull(boolean port);

        void onProgressChanged(float currentTime, float duration);

        void onGetVideoInfo(int width, int height, long duration);

        void progress(float percent);

        void onClickEvent(boolean playing, boolean fullScreen, float currentTime);

        void onDoubleClick(boolean playing, boolean fullScreen, float currentTime);

        void onPosterLoaded(boolean isSuccess);
    }
}

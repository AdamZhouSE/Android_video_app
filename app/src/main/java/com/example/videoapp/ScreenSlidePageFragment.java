package com.example.videoapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.videoapp.data.VideoResponse;
import com.example.videoapp.player.VideoPlayerIJK;
import com.example.videoapp.player.VideoPlayerListener;
import com.example.videoapp.utils.MyClickListener;
import com.example.videoapp.widget.LoveView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 *
 *
 * Fragment页面
 * 重要: ViewPager2 Fragment的生命周期
 * 在进入下一个界面后，前一个界面会运行onPause() 所以需要在onPause下判断如果视频仍在播放，需要停止
 * 在进入第四个Fragment时，第一个界面会运行onStop() onDestroy()，原因是:
 * 在RecycleView中可以发现mViewCacheMax默认是2，也就是说最大缓存数量是2 可以自定义
 *
 * TODO: 快速切换fragment报错 The application may be doing too much work on its main thread. 如何解决？
 * TODO: 能否根据视频加载情况对于出现加载动画
 *
 */

public class ScreenSlidePageFragment extends Fragment {

    private static final String TAG = "FragmentLifeCycle";

    private VideoPlayerIJK videoPlayerIJK;
    private VideoResponse.Video video;
    private SeekBar seekBar;
    private TextView textView;
    private TextView nickname;
    private TextView description;
    private TextView likeCount;
    private ImageButton avatar;
    private ImageButton like;
    private Button buttonPlay;
    private ImageView imagePause;
    private AnimatorSet animatorSet;
    private AnimatorSet animatorSet1;
    private ImageView background;
    private LottieAnimationView animationLoad;
    private LoveView loveView;
    private ImageButton share;


    // 开启一个新线程，每500ms判定一次，使得进度条位置随视频播放变化
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (videoPlayerIJK.isPlaying()) {

                double curPos = (double) videoPlayerIJK.getCurrentPosition();
                double total = (double) videoPlayerIJK.getDuration();
                int currentPos = (int) (curPos / total * 100);
                Log.d("zzy", "curr " + currentPos + " total " + total);
                seekBar.setProgress(currentPos);

                SimpleDateFormat sf = new SimpleDateFormat("mm:ss");

                String curTime = sf.format(videoPlayerIJK.getCurrentPosition());
                String totalTime = sf.format(videoPlayerIJK.getDuration());
                String show = curTime + "/" + totalTime;
                Log.d("zzy", "date: " + curTime + "/" + totalTime);
                textView.setText(show);
            }
            handler.postDelayed(runnable, 500);
        }
    };

    public ScreenSlidePageFragment(VideoResponse.Video video) {
        this.video = video;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);
        videoPlayerIJK = view.findViewById(R.id.ijkPlayer);
        textView = view.findViewById(R.id.textView);
        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.onStop();
        }
        videoPlayerIJK.setListener(new VideoPlayerListener());
        videoPlayerIJK.setVideoPath(video.url);
        videoPlayerIJK.setVisibility(View.GONE);

        // 开启线程
        runnable.run();

        nickname = view.findViewById(R.id.nickname);
        String name = "@" + video.nickname;
        nickname.setText(name);
        description = view.findViewById(R.id.description);
        description.setText(video.description);

        avatar = view.findViewById(R.id.avatar);
        background = view.findViewById(R.id.glide_background);

        like = view.findViewById(R.id.like);
        // 初始化未点赞状态 标签为unlike
        like.setTag("unlike");


        animationLoad = view.findViewById(R.id.animation_load);

        loveView = view.findViewById(R.id.love_view);
        loveView.setVisibility(View.GONE);
        // 使用Glide加载用户头像
        String picUrl = video.avatar.replaceFirst("http", "https");
        RequestOptions cropOptions = new RequestOptions();
        cropOptions = cropOptions.circleCrop();
        Glide.with(this)
                .load(picUrl)
                .apply(cropOptions)
                .error(R.mipmap.avatar)
                .into(avatar);
        resetAvatarAnimation();

        likeCount = view.findViewById(R.id.likeCount);
        if(video.likeCount<10000) {//点赞数低于10000
            likeCount.setText(String.valueOf(video.likeCount));
        }else{
            //以万为单位
            if(video.likeCount%10000==0){//整除一万
                likeCount.setText(video.likeCount/10000+"w");
            }else{//保留一位小数
                float num =(float)video.likeCount/10000;
                DecimalFormat df = new DecimalFormat("0.0");
                likeCount.setText(df.format(num)+"w");
            }
        }

        // 视频暂停时显示暂停图标
        imagePause = view.findViewById(R.id.imagePause);

        // 单击播放/暂停 双击点赞
        buttonPlay = view.findViewById(R.id.button3);
        buttonPlay.setOnTouchListener(new MyClickListener(new MyClickListener.MyClickCallBack() {
            @Override
            public void oneClick() {
                if (videoPlayerIJK.isPlaying()) {
                    videoPlayerIJK.pause();
                    imagePause.setVisibility(View.VISIBLE);
                    resetPauseImageAnimation();
                }
                else {
                    videoPlayerIJK.start();
                    imagePause.setVisibility(View.GONE);
                }
            }

            @Override
            public void doubleClick() {
                like.setBackgroundResource(R.mipmap.like);
                // 点赞后标签变为like
                like.setTag("like");
            }

            @Override
            public void setXY(float x, float y) {
                Log.d("setXY", "" + x + " " + y);
                loveView.setVisibility(View.VISIBLE);
                loveView.setXY(x, y);
                loveView.postInvalidate();
                resetLoveAnimation();
            }
        }));

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (like.getTag().toString().equals("unlike")) {
                    like.setBackgroundResource(R.mipmap.like);
                    like.setTag("like");
                }
                else {
                    like.setBackgroundResource(R.mipmap.beforelike);
                    like.setTag("unlike");
                }
            }
        });

        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            // 进度条在停止移动后视频到达指定时间
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                long time = progress * videoPlayerIJK.getDuration() / 100;
                Log.d("zzy: seekBar", "" + videoPlayerIJK.getCurrentPosition());
                videoPlayerIJK.seekTo(time);
            }
        });

        share = view.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_STREAM, video.url);
                intent = Intent.createChooser(intent, "分享到");
                startActivity(intent);
            }
        });

        Log.d(TAG, "onCreateView() called with: inflater = [" + inflater + "], " + "container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        return view;
    }

    /**
     * 使用animation动画实现暂停图标淡入效果
     */
    private void resetPauseImageAnimation() {
        if (animatorSet != null) {
            animatorSet.cancel();
        }

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(imagePause,
                "scaleX", 2.5f, 1.5f);
        //scaleXAnimator.setDuration(1000);
        scaleXAnimator.setRepeatCount(0);
        scaleXAnimator.setInterpolator(new LinearInterpolator());

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(imagePause,
                "scaleY", 2.5f, 1.5f);
        //scaleXAnimator.setDuration(1000);
        scaleYAnimator.setRepeatCount(0);
        scaleYAnimator.setInterpolator(new LinearInterpolator());

        ObjectAnimator alpha = ObjectAnimator.ofFloat(imagePause,
                "alpha", 0.0f, 0.8f);
        //alpha.setDuration(1000);
        alpha.setRepeatCount(0);
        scaleYAnimator.setInterpolator(new LinearInterpolator());

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alpha);
        animatorSet.start();

    }

    /**
     * 实现头像旋转动画效果
     */

    private void resetAvatarAnimation() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(avatar,
                "rotation", 0, 360);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(8000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }

    /**
     * 实现双击出现爱心图标后的淡出效果
     */

    private void resetLoveAnimation() {

        ObjectAnimator animatorLoveOut = ObjectAnimator.ofFloat(loveView,
                "alpha", 1.0f, 0.0f);
        animatorLoveOut.setDuration(1200);
        animatorLoveOut.setRepeatCount(0);
        animatorLoveOut.start();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach() called with: context = [" + context + "]");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(animationLoad,
                        "alpha", 1.0f, 0.0f);
                animator.setDuration(1000);
                animator.setRepeatCount(0);
                animator.start();

                videoPlayerIJK.setVisibility(View.VISIBLE);
            }
        }, 1000);
        Log.d(TAG, "onActivityCreated() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    /**
     * 切换Fragment时调用，使得当前Fragment视频开始播放
     */
    @Override
    public void onResume() {
        super.onResume();
        imagePause.setVisibility(View.GONE);
        if (!videoPlayerIJK.isPlaying()) {
            videoPlayerIJK.start();
        }
        Log.d(TAG, "onResume() called");
    }

    /**
     * 切换Fragment时调用，使得前一个Fragment播放的视频暂停
     */
    @Override
    public void onPause() {
        super.onPause();
        if (videoPlayerIJK.isPlaying()) {
            videoPlayerIJK.pause();
        }
        IjkMediaPlayer.native_profileEnd();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (videoPlayerIJK.isPlaying()) {
            videoPlayerIJK.stop();
            videoPlayerIJK.release();
        }
        IjkMediaPlayer.native_profileEnd();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach() called");
    }

}

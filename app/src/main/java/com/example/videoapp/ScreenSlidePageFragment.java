package com.example.videoapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.videoapp.data.VideoResponse;
import com.example.videoapp.player.VideoPlayerIJK;
import com.example.videoapp.player.VideoPlayerListener;

import java.text.SimpleDateFormat;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Fragment页面
 * 重要: ViewPager2 Fragment的生命周期
 * 在进入下一个界面后，前一个界面会运行onPause() 所以需要在onPause下判断如果视频仍在播放，需要停止
 * 在进入第四个Fragment时，第一个界面会运行onStop() onDestroy()，原因是:
 * 在RecycleView中可以发现mViewCacheMax默认是2，也就是说最大缓存数量是2
 */

public class ScreenSlidePageFragment extends Fragment {

    private static final String TAG = "FragmentLifeCycle";

    private VideoPlayerIJK videoPlayerIJK;

    private VideoResponse.Video video;

    private SeekBar seekBar;

    private TextView textView;

    private ImageButton buttonPlay;

    private TextView nickname;

    private TextView description;

    private TextView likeCount;

    private ImageButton avatar;

    private ImageButton like;

    // 开启一个新线程，每500ms判定一次，使得进度条位置随视频播放变化
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (videoPlayerIJK.isPlaying()) {
                double curPos = (double) videoPlayerIJK.getCurrentPosition();
                double total = (double) videoPlayerIJK.getDuration();
                int currentPos = (int) (curPos / total * 100);
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

        // 开启线程
        runnable.run();

        nickname = view.findViewById(R.id.nickname);
        nickname.setText(video.nickname);
        description = view.findViewById(R.id.description);
        description.setText(video.description);
        avatar = view.findViewById(R.id.avatar);

        // 使用Glide加载头像
        String picUrl = video.avatar.replaceFirst("http", "https");
        RequestOptions cropOptions = new RequestOptions();
        cropOptions = cropOptions.circleCrop();
        Glide.with(this)
                .load(picUrl)
                .apply(cropOptions)
                .error(R.mipmap.avatar)
                .into(avatar);


        likeCount = view.findViewById(R.id.likeCount);
        likeCount.setText(String.valueOf(video.likeCount));

        // 播放按钮图片随点击改变
        buttonPlay = view.findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPlayerIJK.isPlaying()) {
                    videoPlayerIJK.pause();
                    buttonPlay.setBackgroundResource(R.mipmap.pause3);
                }
                else {
                    videoPlayerIJK.start();
                    buttonPlay.setBackgroundResource(R.mipmap.start3);
                }
            }
        });


        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            // 进度条在停止移动后视频到达指定时间
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                long time = progress * videoPlayerIJK.getDuration() / 100;
                videoPlayerIJK.seekTo(time);
            }
        });
        Log.d(TAG, "onCreateView() called with: inflater = [" + inflater + "], " + "container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        return view;
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
        Log.d(TAG, "onActivityCreated() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

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
            videoPlayerIJK.pause();
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

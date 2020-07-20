package com.example.videoapp.player;

import android.util.Log;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoPlayerListener implements
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnSeekCompleteListener {
    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
        Log.d("videoPlay", "" + i);
        System.out.println(i);
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        Log.d("videoPlay", "onCompletion() called");
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        Log.d("videoPlay", "onInfo() called" + " " + i + " " + i1);

        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
//        iMediaPlayer.start();
        Log.d("videoPlay", "onPrepared() called");
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        Log.d("videoPlay", "onSeekComplete() called");

    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        Log.d("videoPlay", "onchange() called");

    }
}

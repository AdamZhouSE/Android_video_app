package com.example.videoapp.utils;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 判断用户单击屏幕还是双击屏幕
 * 通过handler记录延时来判断用户在一段时间内点击了几次
 */

public class MyClickListener implements View.OnTouchListener {

    private static int timeout=400;//双击间四百毫秒延时
    private int clickCount = 0;//记录连续点击次数
    private Handler handler;
    private MyClickCallBack myClickCallBack;

    public interface MyClickCallBack{
        void oneClick();//点击一次的回调
        void doubleClick();//连续点击两次的回调
        void setXY(float x, float y);
    }

    public MyClickListener(MyClickCallBack myClickCallBack) {
        this.myClickCallBack = myClickCallBack;
        handler = new Handler();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("event", "" + motionEvent.getX() + " " + motionEvent.getY());
            clickCount++;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clickCount == 1) {
                        myClickCallBack.oneClick();
                    }
                    else if (clickCount == 2) {
                        myClickCallBack.doubleClick();
                        myClickCallBack.setXY(motionEvent.getRawX(), motionEvent.getRawY());
                    }
                    // 清空handler延时,防止内存泄露
                    handler.removeCallbacksAndMessages(null);
                    // 计数清零
                    clickCount = 0;
                }
            }, timeout);
        }
        return false;
    }


}

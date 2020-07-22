package com.example.videoapp;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.videoapp.data.ApiService;
import com.example.videoapp.data.VideoResponse;
import com.example.videoapp.player.VideoPlayerIJK;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 *
 */

public class ScreenSlidePagerActivity extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int NUM_PAGES;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 viewPager2;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private FragmentStateAdapter pagerAdapter;

    private List<VideoResponse.Video> videos;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        getData();

        viewPager2 = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager2.setAdapter(pagerAdapter);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        //TabLayout tabLayout = findViewById(R.id.tab_layout);

        //如果是从recycler界面跳转过来，判断当前选中视频的position
        position = getIntent().getIntExtra("position",-1);
        Handler handler = new Handler();
        if(position != -1) {
            ChangeFragmentThread thread=new ChangeFragmentThread(position);
            handler.postDelayed(thread,1000);
            //如果不用线程，直接setCurrentItem会不起作用，网上说是因为未加载完全
        }
        Log.d("position",String.valueOf(position));

    }


    private class ChangeFragmentThread extends Thread {
        private int position;

        public ChangeFragmentThread(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            viewPager2.setCurrentItem(position, false);
        }
    }

    @Override
    public void onBackPressed() {
        // 如果是第一个视频或者从视频列表跳转过来，那么直接返回视频列表界面
        if (viewPager2.getCurrentItem() == 0 || position != -1) {
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
        }
    }


    public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            // 与Fragment通信，传入对应页面的视频信息
            Log.d("Fragment", "" + position);
            VideoResponse.Video video = videos.get(position);
            return new ScreenSlidePageFragment(video);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    /**
     * 获取视频数据
     */
    private void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://beiyou.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getVideos().enqueue(new Callback<List<VideoResponse.Video>>() {
            @Override
            public void onResponse(Call<List<VideoResponse.Video>> call, Response<List<VideoResponse.Video>> response) {
                videos = response.body();
                Log.d("retrofit", videos.toString());
                // fragment数量
                NUM_PAGES = videos.size();
                //!!! 注意这里一定要声明数据改
                pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<VideoResponse.Video>> call, Throwable t) {
                Log.d("retrofit_error", t.getMessage());
            }
        });
    }
}

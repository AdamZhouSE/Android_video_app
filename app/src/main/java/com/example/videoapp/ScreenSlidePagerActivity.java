package com.example.videoapp;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.videoapp.data.ApiService;
import com.example.videoapp.data.VideoResponse;
import com.example.videoapp.player.VideoPlayerIJK;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 使用ViewPager2实现类似抖音的效果
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        getData();

        viewPager2 = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager2.setAdapter(pagerAdapter);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);


        int position=getIntent().getIntExtra("position",-1);
        //如果是从recycler界面跳转过来，判断当前选中视频的position
        if(position!=-1){
            viewPager2.setCurrentItem(position);
        }
        Log.d("position",String.valueOf(position));

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (viewPager2.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
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

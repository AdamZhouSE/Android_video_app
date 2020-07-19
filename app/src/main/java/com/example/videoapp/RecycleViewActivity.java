package com.example.videoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.videoapp.MyAdapter;
import com.example.videoapp.data.ApiService;
import com.example.videoapp.data.VideoResponse;
import com.google.gson.Gson;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecycleViewActivity extends AppCompatActivity implements MyAdapter.ListItemClickListener {

    private static final String TAG = "zzy";
    private static final int NUM_LIST_ITEMS = 100;

    private MyAdapter mAdapter;
    private RecyclerView mNumbersListView;
    private List<VideoResponse.Video> videos;

    private Toast mToast;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycleview);
        mNumbersListView = findViewById(R.id.video_lists);

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2,
                        StaggeredGridLayoutManager.VERTICAL);
        mNumbersListView.setLayoutManager(staggeredGridLayoutManager);
        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNumbersListView.setHasFixedSize(true);

        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new MyAdapter(NUM_LIST_ITEMS, this);

        mNumbersListView.setAdapter(mAdapter);
        mNumbersListView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            // 最后一个完全可见项的位置
            private int lastCompletelyVisibleItemPosition;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (visibleItemCount > 0 && lastCompletelyVisibleItemPosition >= totalItemCount - 1) {
                        Toast.makeText(RecycleViewActivity.this, "已滑动到底部!,触发loadMore", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    lastCompletelyVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                }
                Log.d(TAG, "onScrolled: lastVisiblePosition=" + lastCompletelyVisibleItemPosition);
            }
        });

        getData();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Log.d(TAG, "onListItemClick: ");
        if (mToast != null) {
            mToast.cancel();
        }
        String toastMessage = "Item #" + clickedItemIndex + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
        mToast.show();
        Intent intent=new Intent(RecycleViewActivity.this,ScreenSlidePagerActivity.class);
        intent.putExtra("position",clickedItemIndex);
        startActivity(intent);
    }

    private void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://beiyou.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getVideos().enqueue(new Callback<List<VideoResponse.Video>>() {
            @Override
            public void onResponse(Call<List<VideoResponse.Video>> call, Response<List<VideoResponse.Video>> response) {
                List<VideoResponse.Video> videos = response.body();
                Log.d("retrofit", videos.toString());
                if (videos.size() != 0) {
                    mAdapter.setDate(videos);
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<VideoResponse.Video>> call, Throwable t) {
                Log.d("retrofit_error", t.getMessage());
            }
        });
    }


}

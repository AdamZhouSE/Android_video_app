package com.example.videoapp;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.videoapp.MyAdapter;
import com.example.videoapp.data.ApiService;
import com.example.videoapp.data.VideoResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.List;
import java.util.UUID;

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
    private FloatingActionButton recordVideo;

    private LottieAnimationView animationLoad;
    private Toast mToast;

    public static final int PLAY_CEDE = 1;
    private String fileName;
    //存放文件路径
    private List<String> fileData;





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

        animationLoad=findViewById(R.id.animation_load);
        /*
         * The GreenAdapter is responsible for displaying each item in the list.
         */
        mAdapter = new MyAdapter(NUM_LIST_ITEMS, this);

        getData();
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

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(animationLoad,
                        "alpha", 1.0f, 0.0f);
                animator.setDuration(1000);
                animator.setRepeatCount(0);
                animator.start();

                mNumbersListView.setVisibility(View.VISIBLE);
            }
        }, 1000);

        //初始化录制功能
        initRecordView();


    }
    private String[] mPermissionsArrays = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.VIBRATE
    };

    private final static int REQUEST_PERMISSION = 0x123;

    private boolean checkPermissionAllGranted(String[] permissions) {
        // 6.0以下不需要
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
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
                videos = response.body();
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

    /**

     * 初始化录制视频按钮

     */

    private void initRecordView() {

        //设置显示视频显示在SurfaceView上

        recordVideo=findViewById(R.id.floatingActionButton);
        recordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!checkPermissionAllGranted(mPermissionsArrays)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(mPermissionsArrays, REQUEST_PERMISSION);
//                    } else {
//                        // TODO
//                    }
//                }
//                if(checkPermissionAllGranted(mPermissionsArrays)){
//                    Intent intent = new Intent(RecycleViewActivity.this, RecordingActivity.class);
//                    startActivityForResult(intent, PLAY_CEDE);
//                }
                startActivity(new Intent(RecycleViewActivity.this, RecordVideoActivity.class));

            }
        });

    }


    /**
     * 录制完成的回掉
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLAY_CEDE) {                  //录制视频完成的回掉
            fileName = data.getStringExtra("fileName");
            fileData.add(fileName);
            Toast.makeText(this,"录制成功",Toast.LENGTH_SHORT).show();
            Log.d("record","成功");
        }
    }

    /**
     * 生成随机文件
     *
     * @return
     */
    private File createMFile() {

        //设置文件存放目录

        String fileDir = Environment.getExternalStorageDirectory() + "/videoRecorder";

        File dir = new File(fileDir);

        //判断文件夹是否存在 如不存在就创建

        if (!dir.exists()) {

            dir.mkdir();

        }
        String fileName = generateFileName();
        File mFile = new File(dir, fileName);
       // mCurrentScreenFileName = mFile.getAbsolutePath();
        return mFile;

    }



    /**
     * 随机产生文件名
     *
     * @return
     */
    private String generateFileName(){
        return UUID.randomUUID().toString() + ".mp4";
    }
}

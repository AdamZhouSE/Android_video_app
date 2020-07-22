package com.example.videoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RecordVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button uploadBtn;
    private Uri videoUri;
    private static final int REQUEST_VIDEO_CAPTURE = 1;

    private static final int REQUEST_EXTERNAL_CAMERA = 101;

    private final static int REQUEST_PERMISSION = 0x123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);

        videoView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(RecordVideoActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //todo 在这里申请相机、存储的权限
                ActivityCompat.requestPermissions(RecordVideoActivity.this,mPermissionsArrays,REQUEST_PERMISSION);
            } else {
                //todo 打开相机拍摄
                Intent takeVideoIntent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if(takeVideoIntent.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(takeVideoIntent,REQUEST_VIDEO_CAPTURE);
                }
            }
        });
        uploadBtn=findViewById(R.id.btn_upload);
        uploadBtn.setOnClickListener(v ->{
            if(videoUri==null){
                Toast.makeText(this,"没有视频可以上传！",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"此功能还未完成！",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private String[] mPermissionsArrays = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            //todo 播放刚才录制的视频
            videoUri =intent.getData();
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_CAMERA: {
                //todo 判断权限是否已经授予
                break;
            }
        }
    }
}

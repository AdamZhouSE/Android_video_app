package com.example.videoapp.data;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    // https://beiyou.bytedance.com/api/invoke/video/invoke/video
    // 注意读取格式为数组
    @GET("api/invoke/video/invoke/video")
    Call<List<VideoResponse.Video>> getVideos();
}

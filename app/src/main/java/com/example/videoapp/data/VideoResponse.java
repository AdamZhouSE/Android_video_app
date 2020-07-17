package com.example.videoapp.data;


import com.google.gson.annotations.SerializedName;

/**
 * 获取视频信息
 */

public class VideoResponse {

    @SerializedName("errorCode")
    public int errorCode;
    @SerializedName("errorMsg")
    public String errorMsg;

    public static class Video {
        @SerializedName("_id")
        public String id;
        @SerializedName("feedurl")
        public String url;
        @SerializedName("nickname")
        public String nickname;
        @SerializedName("description")
        public String description;
        @SerializedName("likecount")
        public int likeCount;
        @SerializedName("avatar")
        public String avatar;

        @Override
        public String toString() {
            return "Video{" +
                    "id='" + id + "'" +
                    ", url='" + url + "'" +
                    ", nickname='" + nickname + "'" +
                    ", description='" + description + "'" +
                    ", likeCount=" + likeCount +
                    ", avatar='" + avatar + "'" +
                    "}";
        }

    }


}

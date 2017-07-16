package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Marcin on 16.07.2017.
 */

public class UserVideoMsg extends BaseMsg{

    @SerializedName("title")
    private String title;
    @SerializedName("video")
    private String videoUrl;
    @SerializedName("author")
    private String author;
    @SerializedName("created_at")
    private Date created;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public class UserVideosMsg{
        @SerializedName("user_videos")
        private List<UserVideoMsg> videoMsgList;

        public List<UserVideoMsg> getVideoMsgList() {
            return videoMsgList;
        }

        public void setVideoMsgList(List<UserVideoMsg> videoMsgList) {
            this.videoMsgList = videoMsgList;
        }
    }



}

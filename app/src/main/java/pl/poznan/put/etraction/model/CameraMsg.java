package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Marcin on 19.04.2017.
 */

public class CameraMsg {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class CamerasMsg {

        @SerializedName("camera")
        List<CameraMsg> cameras;

        public List<CameraMsg> getCameras() {
            return cameras;
        }

        public void setCameras(List<CameraMsg> cameras) {
            this.cameras = cameras;
        }
    }
}

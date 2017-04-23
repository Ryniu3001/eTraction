package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Marcin on 19.04.2017.
 */

public class CameraMsg extends BaseMsg {

    @SerializedName("name")
    private String name;

    @SerializedName("url")
    private String url;


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

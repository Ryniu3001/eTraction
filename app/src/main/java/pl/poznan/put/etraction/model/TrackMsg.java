package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Marcin on 09.07.2017.
 */

public class TrackMsg extends BaseMsg {

    @SerializedName("total_travel_time")
    private Integer totalTravelTime;
    @SerializedName("track_items")
    private List<TrackItemMsg> trackItems;


    public Integer getTotalTravelTime() {
        return totalTravelTime;
    }

    public void setTotalTravelTime(Integer totalTravelTime) {
        this.totalTravelTime = totalTravelTime;
    }

    public List<TrackItemMsg> getTrackItems() {
        return trackItems;
    }

    public void setTrackItems(List<TrackItemMsg> trackItems) {
        this.trackItems = trackItems;
    }

    /**
     * Used for deserialize JSON
     */
    public static class Track{
        @SerializedName("track")
        TrackMsg track;
        public TrackMsg getTrack() { return track; }
        public void setTrack(TrackMsg track) { this.track = track; }

    }
}


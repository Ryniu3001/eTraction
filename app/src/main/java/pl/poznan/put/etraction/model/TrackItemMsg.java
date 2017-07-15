package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Marcin on 09.07.2017.
 */

public class TrackItemMsg {

    @SerializedName("position")
    private Integer position;
    @SerializedName("stop_name")
    private String stopName;
    @SerializedName("lat")
    private Double lat;
    @SerializedName("lon")
    private Double lon;
    @SerializedName("arrival_time")
    private Date arrivalTime;
    @SerializedName("departure_time")
    private Date departureTime;


    public TrackItemMsg() {
    }

    public TrackItemMsg(Integer position, String stopName, Double lat, Double lon) {
        this.position = position;
        this.stopName = stopName;
        this.lat = lat;
        this.lon = lon;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }
}

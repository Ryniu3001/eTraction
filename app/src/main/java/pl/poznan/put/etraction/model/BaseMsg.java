package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Marcin on 23.04.2017.
 */

abstract class BaseMsg {
    @SerializedName("id")
    protected int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Marcin on 23.04.2017.
 */

public class RestaurantMenuItemMsg extends BaseMsg {

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private Double price;

    @SerializedName("image")
    private String imageUrl;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static class RestaurantMenuItemsMsg {

        @SerializedName("restaurant_menu_items")
        List<RestaurantMenuItemMsg> items;

        public List<RestaurantMenuItemMsg> getItems() {
            return items;
        }

        public void setItems(List<RestaurantMenuItemMsg> items) {
            this.items = items;
        }
    }
}

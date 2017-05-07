package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Marcin on 06.05.2017.
 */

public class UserMsg {
    @SerializedName("user")
    private User user;

    public UserMsg(){
        user = new User();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return getUser().getUsername();
    }

    public void setUsername(String username) {
        getUser().setUsername(username);
    }

    private class User  extends BaseMsg {
        @SerializedName("username")
        private String username;

        private String getUsername() {
            return username;
        }

        private void setUsername(String username) {
            this.username = username;
        }
    }
}

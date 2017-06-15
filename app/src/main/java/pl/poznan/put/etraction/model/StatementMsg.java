package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Marcin on 12.04.2017.
 */

public class StatementMsg extends BaseMsg{


    @SerializedName("title")
    private String title;

    @SerializedName("created_at")
    private Date dateTime;

    @SerializedName("text")
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static class StatementMessageDTO {
        @SerializedName("statement")
        private StatementMsg message;

        public StatementMsg getMessage() {
            return message;
        }

        public void setMessage(StatementMsg message) {
            this.message = message;
        }
    }
}
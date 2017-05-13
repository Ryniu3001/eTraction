package pl.poznan.put.etraction.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Marcin on 01.05.2017.
 */
public class ChatMessageMsg extends BaseMsg {
    @SerializedName("author")
    private String author;
    @SerializedName("created_at")
    private Date date;
    @SerializedName("text")
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static class ChatMessagesMsg {
        @SerializedName("messages")
        private List<ChatMessageMsg> chatMessages;

        public List<ChatMessageMsg> getChatMessages() {
            return chatMessages;
        }

        public void setChatMessages(List<ChatMessageMsg> chatMessages) {
            this.chatMessages = chatMessages;
        }
    }

    public static class ChatMessageDTO {
        @SerializedName("message")
        private ChatMessageMsg message;

        public ChatMessageMsg getMessage() {
            return message;
        }

        public void setMessage(ChatMessageMsg message) {
            this.message = message;
        }
    }
}

package pl.poznan.put.etraction.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Marcin on 01.05.2017.
 */

public class ChatMessageMsg extends BaseMsg {

    private String author;
    private Date date;
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

        private List<ChatMessageMsg> chatMessages;

        public List<ChatMessageMsg> getChatMessages() {
            return chatMessages;
        }

        public void setChatMessages(List<ChatMessageMsg> chatMessages) {
            this.chatMessages = chatMessages;
        }
    }
}

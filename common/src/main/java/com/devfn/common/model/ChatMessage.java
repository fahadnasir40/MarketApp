package com.devfn.common.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage implements Serializable {

    private String chatId,key;
    private String message,status;
    private String timeStamp;
    private boolean isSeen;

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean isSeen) {
        this.isSeen = isSeen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    //private PostItem postItem;


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ChatMessage() {
        chatId = "";
        isSeen = false;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDateFromTimeStamp(String datePattern){
        if(datePattern.equals(""))
            datePattern = "dd MMM YYYY  HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        return formatter.format(new Date(Long.parseLong(this.getTimeStamp())));
    }

    public ChatMessage(String chatId, String key,String message, String status, String timeStamp,boolean isSeen) {
        this.key = key;
        this.chatId = chatId;
        this.message = message;
        this.status = status;
        this.isSeen = isSeen;
        this.timeStamp = timeStamp;
    }
}

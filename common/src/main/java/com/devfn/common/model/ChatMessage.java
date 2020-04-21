package com.devfn.common.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessage {

    private String chatId;
    private String message,status;
    private String timeStamp;
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

    public String getDateFromTimeStamp(){

        String datePattern = "dd MMM YYYY  HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        return formatter.format(new Date(Long.parseLong(this.getTimeStamp())));
    }

    public ChatMessage(String chatId, String message, String status, String timeStamp) {
        this.chatId = chatId;
        this.message = message;
        this.status = status;
        this.timeStamp = timeStamp;
    }
}

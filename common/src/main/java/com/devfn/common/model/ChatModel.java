package com.devfn.common.model;

import java.util.HashMap;

public class ChatModel {

    private String chatId,senderId,messageType,senderName,createTime;
    private HashMap<String, ChatMessage> messages;
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public ChatModel(String chatId, String senderId, String messageType, String senderName) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.messages = new HashMap<String,ChatMessage>();
        this.messageType = messageType;
        this.senderName = senderName;
    }
}

package com.devfn.common.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChatModel {

    private String chatId,senderId,messageType,senderName,createTime;
    private Map<String, ChatMessage> messages;
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

    public Map<String, ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, ChatMessage> messages) {
        this.messages = messages;
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
        this.messages = new LinkedHashMap<String,ChatMessage>();
        this.messageType = messageType;
        this.senderName = senderName;
    }

    public ChatModel() {
        this.messages = new LinkedHashMap<String,ChatMessage>();
        this.senderName = "";
    }

    public void addMessage(ChatMessage chatMessage,String key) {

        this.messages.put(key,chatMessage);
    }


    public boolean removeMessage(String key) {
        messages.remove(key);
        return true;
    }
}

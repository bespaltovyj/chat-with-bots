package com.rnd.domain;


public class Message {
    private Type type;
    private String roomId;
    private String userLogin;
    private String content;

    public Message() {
    }

    public Message(Type type, String roomId, String userLogin, String content) {
        this.type = type;
        this.roomId = roomId;
        this.userLogin = userLogin;
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

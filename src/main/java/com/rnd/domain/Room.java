package com.rnd.domain;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String id;
    private List<WebSocketSession> userSessions = new ArrayList<>();
    private List<WebSocketSession> botSessions = new ArrayList<>();

    public Room(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<WebSocketSession> getUserSessions() {
        return userSessions;
    }

    public List<WebSocketSession> getBotSessions() {
        return botSessions;
    }

    public boolean addUserSession(WebSocketSession session) {
        return userSessions.add(session);
    }

    public boolean removeUserSession(WebSocketSession session) {
        return userSessions.remove(session);
    }

    public boolean addBotSession(WebSocketSession session) {
        return botSessions.add(session);
    }

    public boolean removeBotSession(WebSocketSession session) {
        return botSessions.remove(session);
    }
}

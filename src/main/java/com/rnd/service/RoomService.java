package com.rnd.service;

import com.rnd.domain.Room;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Service
public class RoomService {

    private HashMap<String, Room> rooms = new HashMap<>();

    public String createRoom() {
        String id = UUID.randomUUID().toString();
        Room room = new Room(id);
        rooms.put(id, room);
        return id;
    }

    public boolean checkAccessToRoom(String loginMailSender, String roomId, boolean isBot) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Invalid room id");
        }
        List<WebSocketSession> sessions = isBot ? room.getBotSessions() : room.getUserSessions();
        // Is the user who invited another in the same chat
        return sessions.stream()
                .map(session1 -> UserService.getUsernameFromPrincipal(session1.getPrincipal()))
                .anyMatch(o -> Objects.equals(loginMailSender, o));
    }

    public boolean connectToRoom(String roomId, boolean isBot, WebSocketSession session) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Invalid room id");
        }
        return isBot ? room.addBotSession(session) : room.addUserSession(session);
    }


    public void sendMessage(String roomId, boolean isBot, TextMessage message) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Invalid room id");
        }
        List<WebSocketSession> sessions = isBot ? room.getBotSessions() : room.getUserSessions();
        sendMessage(sessions, message);
    }

    private void sendMessage(List<WebSocketSession> sessions, TextMessage message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void userLogout(WebSocketSession sessionOut) {
        Iterator<Map.Entry<String, Room>> it = rooms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Room> entry = it.next();
            Room room = entry.getValue();
            room.removeUserSession(sessionOut);
            //room.getUserSessions().remove(sessionOut);
            if (room.getUserSessions().isEmpty()) {
                it.remove();
            }
        }
    }
}

package com.rnd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private MessageBuilder messageBuilder;

    @Autowired
    private BotService botService;

    private Map<String, WebSocketSession> usersOnline = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    public void userLogin(String login, WebSocketSession session) throws IOException {
        sendUsersOnline(session);
        botService.sendBotsOnline(session);
        sendMessageToAllUser(messageBuilder.getUserLoginMessage(login));
        usersOnline.put(login,session);
    }

    public void userLogout(String login) throws IOException {
        usersOnline.remove(login);
        sendMessageToAllUser(messageBuilder.getUserLogoutMessage(login));
    }

    public WebSocketSession getUserSessionByLogin(String login) {
        return usersOnline.get(login);
    }

    public Set<String> usersOnline() {
        return usersOnline.keySet();
    }

    public void sendUsersOnline(WebSocketSession session) throws IOException {
        session.sendMessage(messageBuilder.getUsersOnlineMessage(usersOnline.keySet()));
    }

    public void sendMessageToAllUser(TextMessage message) throws IOException {
        for (WebSocketSession webSocketSession: usersOnline.values()) {
            webSocketSession.sendMessage(message);
        }
    }

}

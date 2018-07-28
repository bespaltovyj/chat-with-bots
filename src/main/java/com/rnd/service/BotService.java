package com.rnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Service
public class BotService {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageBuilder messageBuilder;

    private Map<String, WebSocketSession> botsOnline = new HashMap<>();

    public WebSocketSession getBotSessionById(String id) {
        return botsOnline.get(id);
    }

    public void botConnect(String botName, WebSocketSession session) throws IOException {
        userService.sendMessageToAllUser(messageBuilder.getUserLoginMessage(botName));
        botsOnline.put(botName,session);
    }


    public void botDisconnect(WebSocketSession session) throws IOException {
        String botName = getBotNameBySession(session);
        botsOnline.remove(botName);

        userService.sendMessageToAllUser(messageBuilder.getUserLogoutMessage(botName));
    }

    public String getBotNameBySession(WebSocketSession session) {
        Principal principal = session.getPrincipal();
        return principal.getName();
    }

    public void sendBotsOnline(WebSocketSession session) throws IOException {
        session.sendMessage(messageBuilder.getUsersOnlineMessage(botsOnline.keySet()));
    }
}

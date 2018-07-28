package com.rnd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnd.domain.Message;
import com.rnd.domain.Type;
import com.rnd.service.BotService;
import com.rnd.service.RoomService;
import com.rnd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Component
public class BotHandler extends TextWebSocketHandler {

    @Autowired
    private BotService botService;

    @Autowired
    private RoomService roomService;

    private ObjectMapper mapper = new ObjectMapper();


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        String botName = botService.getBotNameBySession(session);
        Message message = mapper.readValue(textMessage.getPayload(), Message.class);
        boolean access = roomService.checkAccessToRoom(botName, message.getRoomId(), true);
        if (!access) {
            return;
        }
        message.setUserLogin(botName);
        message.setType(Type.SEND_MESSAGE);
        roomService.sendMessage(message.getRoomId(), false, new TextMessage(mapper.writeValueAsString(message)));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        botService.botConnect(botService.getBotNameBySession(session), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        botService.botDisconnect(session);
    }


}

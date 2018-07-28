package com.rnd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnd.domain.Message;
import com.rnd.service.BotService;
import com.rnd.service.MessageBuilder;
import com.rnd.service.RoomService;
import com.rnd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;

@Component
public class MessageHandler extends TextWebSocketHandler {

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private BotService botService;

    @Autowired
    private MessageBuilder messageBuilder;

    private ObjectMapper mapper = new ObjectMapper();


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        Message message = mapper.readValue(textMessage.getPayload(), Message.class);
        Principal principal = session.getPrincipal();
        String usernameSender = UserService.getUsernameFromPrincipal(principal);
        switch (message.getType()) {
            case CREATE_ROOM: {
                String roomId = roomService.createRoom();
                roomService.connectToRoom(roomId, false, session);
                message.setRoomId(roomId);
                session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
                break;
            }
            case ADD_USER: {
                boolean access = roomService.checkAccessToRoom(usernameSender, message.getRoomId(), false);
                if (access) {
                    boolean isBot = false;
                    WebSocketSession destinationSession = userService.getUserSessionByLogin(message.getUserLogin());
                    if (destinationSession == null) {
                        isBot = true;
                        destinationSession = botService.getBotSessionById(message.getUserLogin());
                    }
                    if (!isBot) {
                        destinationSession.sendMessage(messageBuilder.getCreteRoomMessage(message.getRoomId()));
                    }
                    roomService.connectToRoom(message.getRoomId(), isBot, destinationSession);
                    roomService.sendMessage(message.getRoomId(), false, new TextMessage(mapper.writeValueAsString(message)));
                }
                break;
            }
            case SEND_MESSAGE: {
                boolean access = roomService.checkAccessToRoom(usernameSender, message.getRoomId(), false);
                if (access) {
                    message.setUserLogin(usernameSender);
                    TextMessage responseTextMessage = new TextMessage(mapper.writeValueAsString(message));
                    roomService.sendMessage(message.getRoomId(), false, responseTextMessage);
                    roomService.sendMessage(message.getRoomId(), true, responseTextMessage);
                }
                break;
            }
            case USER_LOGIN: {
                userService.userLogin(message.getUserLogin(), session);
                break;
            }
            case USER_LOGOUT: {
                userService.userLogout(message.getUserLogin());
                break;
            }
            case USERS_ONLINE: {
                message.setContent(mapper.writeValueAsString(userService.usersOnline()));
                session.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
                break;
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Principal principal = session.getPrincipal();
        String username = UserService.getUsernameFromPrincipal(principal);
        userService.userLogin(username, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Principal principal = session.getPrincipal();
        String username = UserService.getUsernameFromPrincipal(principal);
        userService.userLogout(username);
        roomService.userLogout(session);
    }
}

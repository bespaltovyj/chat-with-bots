package com.rnd.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rnd.domain.Message;
import com.rnd.domain.Type;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.util.Set;

@Service
public class MessageBuilder {

    private ObjectMapper mapper = new ObjectMapper();

    public TextMessage getUsersOnlineMessage(Set<String> usersOnline) throws JsonProcessingException {
        Message message = new Message();
        message.setType(Type.USERS_ONLINE);
        message.setContent(mapper.writeValueAsString(usersOnline));
        return new TextMessage(mapper.writeValueAsString(message));
    }

    public TextMessage getCreteRoomMessage(String roomId) throws JsonProcessingException {
        Message createRoomMessage = new Message();
        createRoomMessage.setType(Type.CREATE_ROOM);
        createRoomMessage.setRoomId(roomId);
        return new TextMessage(mapper.writeValueAsString(createRoomMessage));
    }

    public TextMessage getUserLoginMessage(String login) throws JsonProcessingException {
        return createTextMessage(Type.USER_LOGIN, login);
    }

    public TextMessage getUserLogoutMessage(String login) throws JsonProcessingException {
        return createTextMessage(Type.USER_LOGOUT, login);
    }

    private TextMessage createTextMessage(Type type, String login) throws JsonProcessingException {
        Message message = new Message();
        message.setType(type);
        message.setUserLogin(login);
        return new TextMessage(mapper.writeValueAsString(message));
    }
}

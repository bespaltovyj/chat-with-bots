package com.rnd.config;

import com.rnd.controller.BotHandler;
import com.rnd.controller.MessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(webSocketHandler(), "/chats-socket").withSockJS();
        webSocketHandlerRegistry.addHandler(botHandler(),"/bots-socket").withSockJS();
    }

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new MessageHandler();
    }

    @Bean
    public WebSocketHandler botHandler() {
        return new BotHandler();
    }
}

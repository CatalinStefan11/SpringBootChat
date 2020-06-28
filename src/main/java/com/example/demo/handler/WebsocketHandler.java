package com.example.demo.handler;

import com.example.demo.dto.MessageDto;
import com.example.demo.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class WebsocketHandler extends AbstractWebSocketHandler {

    private Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    private final ChatService chatService;

    @Autowired
    public WebsocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    //todo seassion map sa fie tot timpul bun sa fie populat cu conexiuni
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String msg = String.valueOf(message.getPayload());
            MessageDto messageDto = objectMapper.readValue(msg, MessageDto.class);
            log.info(messageDto);
            if (messageDto.getMessage().toLowerCase().equals("connected") && messageDto.getSenderId() != null) {
                log.info("added new user to connection pool userId:" + messageDto.getSenderId() + " sessionId " + session.hashCode());
                sessionMap.put(messageDto.getSenderId(), session);
            } else {
                chatService.saveMessage(messageDto);
                log.info("messaged saved");
                System.out.println(sessionMap.toString());
                if (sessionMap.containsKey(messageDto.getSenderId())) {
                    log.info("msg forwarded from " + messageDto.getSenderId() + " to " + messageDto.getRecipientId());
                    sessionMap.get(messageDto.getSenderId()).sendMessage(new TextMessage(messageDto.toString()));
                } else {
                    log.info("session map does not contain session  for websocket");
                    log.info("queue the message to be sent when the ");
                    chatService.saveMessagesUndelivered(messageDto);
                }
            }
        } catch (Exception e) {
            log.info("smth wrong with deserialization of Message class");
        }
    }
}

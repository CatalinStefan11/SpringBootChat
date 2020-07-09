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

import static com.example.demo.misc.Util.isMessageOfConnection;
import static com.example.demo.misc.Util.removeEndlines;

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

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String msg = removeEndlines(message.getPayload());
            MessageDto messageDto = objectMapper.readValue(msg, MessageDto.class);
            log.info(messageDto);
            if (isMessageOfConnection(messageDto)) {
                log.info("added new user to connection pool userId:" + messageDto.getSenderId() + " sessionId " + session.hashCode());
                sessionMap.put(messageDto.getSenderId(), session);
            } else {
                chatService.saveMessage(messageDto);
                if (sessionMap.containsKey(messageDto.getRecipientId())) {
                    log.info("msg forwarded from " + messageDto.getSenderId() + " to " + messageDto.getRecipientId());

                    if (sessionMap.get(messageDto.getRecipientId()).isOpen()) {
                        sessionMap.get(messageDto.getRecipientId()).sendMessage(new TextMessage(messageDto.toString()));
                    } else {
                        sessionMap.remove(messageDto.getRecipientId());
                    }
                    chatService.saveMessagesUndelivered(messageDto);

                } else {
                    log.info("we queued the messaege");
                    chatService.saveMessagesUndelivered(messageDto);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

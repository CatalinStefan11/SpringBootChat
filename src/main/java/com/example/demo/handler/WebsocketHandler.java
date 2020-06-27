package com.example.demo.handler;

import com.example.demo.dto.MessageDto;
import com.example.demo.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Component
public class WebsocketHandler extends AbstractWebSocketHandler {

    private List<WebSocketSession> sessionList = new CopyOnWriteArrayList<>();
    private Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);
    private ObjectMapper objectMapper = new ObjectMapper();

    private final ChatService chatService;

    @Autowired
    public WebsocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String msg = String.valueOf(message.getPayload());
        try {
            MessageDto messageDto = objectMapper.readValue(msg, MessageDto.class);
            log.info(messageDto);
            chatService.saveMessage(messageDto);
            log.info("messaged saved");
            String senderId = messageDto.getSenderId();
            String recipient = messageDto.getRecipientId();

            if (counter.get() > 0) {
                log.info("added new user to connection pool userId:" + senderId + " sessionId " + session.hashCode());
                sessionMap.put(senderId, session);
                counter.decrementAndGet();
            }

            if (sessionMap.containsKey(recipient)) {
                log.info("msg forwarded from " + messageDto.getSenderId() + " to " + messageDto.getRecipientId());
                sessionMap.get(recipient).sendMessage(new TextMessage(messageDto.toString()));
                log.info("used session " + sessionMap.get(recipient).hashCode());
            } else {
                log.info("recipient " + recipient + " is not in the map");
            }
        } catch (Exception e) {
            log.info(msg);
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionList.add(session);
        counter.incrementAndGet();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionList.remove(session);
        if (sessionMap.containsValue(session))
        {
            sessionMap.entrySet().removeIf(e -> e.getValue().equals(session));
        }
    }

}

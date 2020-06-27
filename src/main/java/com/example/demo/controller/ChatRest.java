package com.example.demo.controller;


import com.example.demo.dto.MessageDto;
import com.example.demo.dto.PageDto;
import com.example.demo.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class ChatRest {

    private final ChatService chatService;

    @Autowired
    public ChatRest(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{senderId}/{recipientId}/messages")
    public List<MessageDto> getConversationHistoryBulk(@PathVariable("senderId") String senderId, @PathVariable("recipientId") String recipientId) {
        log.info("return message history for users: " + senderId + " with " + recipientId);
        return chatService.getMessageHistoryFor(senderId, recipientId);
    }

    @GetMapping("/messages/{senderId}/{recipientId}/page")
    public List<MessageDto> getConversationHistoryByPage(@PathVariable("senderId") String senderId, @PathVariable("recipientId") String recipientId,
                                                         @RequestBody PageDto page) {
        log.info("return message history for users: " + senderId + " with " + recipientId);
        return chatService.getMessageHistoryForWithPage(senderId, recipientId, PageRequest.of(page.getPage(), page.getSize()));
    }

}

package com.example.demo.controller;


import com.example.demo.dto.MessageDto;
import com.example.demo.dto.PageDto;
import com.example.demo.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

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

    @GetMapping("/{recipient}/contacts")
    public Set<String> getContacts(@PathVariable("recipient") String recipient) {
        log.info("return list of users who contacted: " + recipient);
        return chatService.getPersonsContacted(recipient);
    }

    @DeleteMapping("/{recipient}/contacts")
    public void clearContacts(@PathVariable("recipient") String recipient) {
        log.info("cleared the list of users who contacted: " + recipient);
        chatService.clearMessagesUndelivered(recipient);
    }


    @GetMapping("/messages/{senderId}/{recipientId}/page")
    public List<MessageDto> getConversationHistoryByPage(@PathVariable("senderId") String senderId, @PathVariable("recipientId") String recipientId,
                                                         @RequestBody PageDto page) {
        log.info("return message history for users: " + senderId + " with " + recipientId);
        return chatService.getMessageHistoryForWithPage(senderId, recipientId, PageRequest.of(page.getPage(), page.getSize()));
    }

}

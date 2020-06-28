package com.example.demo.service;

import com.example.demo.dto.MessageDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface ChatService {

    List<MessageDto> getMessageHistoryFor(String senderId, String recipientId);

    Set<String> getPersonsContacted(String recipient);

    void saveMessage(MessageDto messageDto);

    List<MessageDto> getMessageHistoryForWithPage(String senderId, String recipientId, Pageable pageable);

    void saveMessagesUndelivered(MessageDto messageDto);

    void clearMessagesUndelivered(String recipient);
}

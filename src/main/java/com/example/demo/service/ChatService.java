package com.example.demo.service;

import com.example.demo.dto.MessageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ChatService {

    List<MessageDto> getMessageHistoryFor(String senderId, String recipientId);

    void saveMessage(MessageDto messageDto);

    List<MessageDto> getMessageHistoryForWithPage(String senderId, String recipientId, Pageable pageable);
}

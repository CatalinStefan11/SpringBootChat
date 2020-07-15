package com.example.demo.service;

import com.example.demo.dto.MessageDto;
import com.example.demo.misc.MessageQueue;
import com.example.demo.model.Conversation;
import com.example.demo.model.Message;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.MessageRepositoryPagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.misc.Util.EMPTY_STRING;
import static com.example.demo.model.Conversation.createConversations;
import static com.example.demo.model.Message.createMessageFrom;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final MessageRepositoryPagination messageRepositoryPagination;
    private final ConversationRepository conversationRepository;
    private final MessageQueue messageQueue;

    private List<Message> messagesCached = new ArrayList<>();

    @Value("${tefnut.batch.size}")
    private Integer batchSize;

    private final long delayMillisecondsSaveBatch = 5_000;

    @Autowired
    public ChatServiceImpl(MessageRepository messageRepository,
                           MessageRepositoryPagination messageRepositoryPagination,
                           ConversationRepository conversationRepository,
                           MessageQueue messageQueue) {
        this.messageRepository = messageRepository;
        this.messageRepositoryPagination = messageRepositoryPagination;
        this.conversationRepository = conversationRepository;
        this.messageQueue = messageQueue;
    }

    @Override
    public List<MessageDto> getMessageHistoryFor(String senderId, String recipientId) {
        return conversationRepository
                .findBySenderAndRecipient(senderId, recipientId)
                .map(conversation -> messageRepository
                        .findByUuid(conversation.getUuid())
                        .stream()
                        .map(message -> new MessageDto(message.getMessage(), message.getRecipientId(), message.getSenderId(), EMPTY_STRING))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    @Override
    public Set<String> getPersonsContacted(String recipient) {
        return messageQueue.getAllPersonsSentMessageTo(recipient);
    }

    @Override
    public void saveMessage(MessageDto messageDto) {

        // todo caching
        Optional<Conversation> conversation = conversationRepository
                .findBySenderAndRecipient(messageDto.getSenderId(), messageDto.getRecipientId());

        if (!conversation.isPresent())
        {
            // cand nu exista
            String uuid = UUID.randomUUID().toString();
            List<Conversation> conversations = Arrays.asList(
                Conversation.builder().recipient(messageDto.getRecipientId()).sender(messageDto.getSenderId()).uuid(uuid).build(),
                Conversation.builder().recipient(messageDto.getSenderId()).sender(messageDto.getRecipientId()).uuid(uuid).build());
            conversationRepository.saveAll(conversations);
            messageDto.setUuid(uuid);
        } else {
            // cand exista deja
            messageDto.setUuid(conversation.get().getUuid());
        }


        messagesCached.add(Message.createMessageFrom(messageDto));
        if (messagesCached.size() >= batchSize) {
            log.info("batch size reached maximum capacity");
            saveMessagesCachedInBatch();
        }
    }

    @Override
    public List<MessageDto> getMessageHistoryForWithPage(String senderId, String recipientId, Pageable pageable) {
        return messageRepositoryPagination.findBySenderIdAndRecipientId(senderId, recipientId, pageable)
                .stream()
                .map(message -> new MessageDto(message.getMessage(), recipientId, message.getSenderId(), EMPTY_STRING))
                .collect(Collectors.toList());
    }

    @Override
    public void saveMessagesUndelivered(MessageDto messageDto) {
        messageQueue.addMessage(createMessageFrom(messageDto));
    }

    @Override
    public void clearMessagesUndelivered(String recipient) {
        messageQueue.removeMessagesFor(recipient);
    }

    @Scheduled(fixedDelay = delayMillisecondsSaveBatch)
    private void saveMessagesCachedInBatch() {
        if (!messagesCached.isEmpty()) {
            log.info("save list of messages in batch");
            messageRepository.saveAll(messagesCached);
            messagesCached.clear();
        }
    }
}

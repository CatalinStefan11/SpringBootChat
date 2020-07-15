package com.example.demo.model;

import com.example.demo.dto.MessageDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties
public class Conversation {

    @Id
    @GeneratedValue
    private Long id;

    private String sender;
    private String recipient;
    private String uuid;

    public static List<Conversation> createConversations(MessageDto messageDto) {
        String uuid = UUID.randomUUID().toString();
        return Arrays.asList(Conversation.builder().recipient(messageDto.getRecipientId()).sender(messageDto.getSenderId()).uuid(uuid).build(),
                Conversation.builder().recipient(messageDto.getSenderId()).sender(messageDto.getRecipientId()).uuid(uuid).build());
    }
}

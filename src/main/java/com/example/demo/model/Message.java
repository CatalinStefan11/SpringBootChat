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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@JsonIgnoreProperties
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    private String message;
    private String senderId;
    private String recipientId;
    private String uuid;

    public static Message createMessageFrom(MessageDto messageDto) {
        return Message.builder()
                .message(messageDto.getMessage())
                .senderId(messageDto.getSenderId())
                .recipientId(messageDto.getRecipientId())
                .uuid(messageDto.getUuid())
                .build();
    }
}

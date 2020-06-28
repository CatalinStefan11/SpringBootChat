package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private String message;
    private String recipientId;
    private String senderId;
    private String uuid;

    @Override
    public String toString() {
        return  "{" +
                "\"message\":" + "\"" + message + "\"" +
                ", " +
                "\"senderId\":" + "\"" + senderId + "\"" +
                ", " +
                "\"recipientId\":" + "\"" + recipientId + "\"" +
                ", " +
                "\"uuid\":" + "\"" + uuid + "\"" +
                '}';
    }
}

package com.example.demo.misc;

import com.example.demo.dto.MessageDto;

public class Util {

    public final static String EMPTY_STRING = "";

    public static String removeEndlines(String message) {
        return message.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");
    }

    public static boolean isMessageOfConnection(MessageDto message) {
        return message.getMessage().toLowerCase().equals("connected") && message.getSenderId() != null;
    }
}

package com.example.demo.misc;

import com.example.demo.model.Message;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class MessageQueue {

    private Map<String, Set<String>> sendersQueuedForRecipients = new HashMap<>();

    public void addMessage(Message message){
        if(sendersQueuedForRecipients.containsKey(message.getRecipientId()))
        {
            Set<String> aux = sendersQueuedForRecipients.get(message.getRecipientId());
            aux.add(message.getSenderId());
            sendersQueuedForRecipients.put(message.getRecipientId(), aux);
        } else {
            Set<String> set = new HashSet<>();
            set.add(message.getSenderId());
            sendersQueuedForRecipients.put(message.getRecipientId(),set);
        }
    }

    public Set<String> getAllPersonsSentMessageTo(String recipient){
        return sendersQueuedForRecipients.getOrDefault(recipient, new HashSet<>());
    }

    public void removeMessagesFor(String recipient){
        sendersQueuedForRecipients.remove(recipient);
    }
}

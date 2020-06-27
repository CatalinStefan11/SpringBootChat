package com.example.demo.repository;

import com.example.demo.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MessageRepositoryPagination extends PagingAndSortingRepository<Message, Long> {

    List<Message> findBySenderIdAndRecipientId(String senderId, String recipientId, Pageable pageable);
}

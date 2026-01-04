package com.merchant.app.repository;

import com.merchant.app.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderEmailAndReceiverEmailOrSenderEmailAndReceiverEmailOrderByTimestampAsc(
            String sender1, String receiver1, String sender2, String receiver2);
}

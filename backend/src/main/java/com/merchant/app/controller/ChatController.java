package com.merchant.app.controller;

import com.merchant.app.entity.ChatMessage;
import com.merchant.app.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);

        // Convert email to a sanitized topic-friendly string if needed, or just use as
        // is if simpler
        // Ideally we use User IDs, but email is fine for now.
        // Send to receiver's queue
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverEmail(), "/queue/messages",
                chatMessage);
    }

    @GetMapping("/api/messages")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getMessages(@RequestParam String sender, @RequestParam String receiver) {
        return ResponseEntity.ok(
                chatMessageRepository.findBySenderEmailAndReceiverEmailOrSenderEmailAndReceiverEmailOrderByTimestampAsc(
                        sender, receiver, receiver, sender));
    }
}

package com.example.chat.controller;

import com.example.chat.entity.Chat_private_entity;
import com.example.chat.service.Chat_private_service;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;


@Controller
@CrossOrigin("*")
public class Chat_private_controller {

    private Chat_private_service service;
    private final SimpMessagingTemplate messagingTemplate;

    public Chat_private_controller(SimpMessagingTemplate messagingTemplate, Chat_private_service service) {
        this.messagingTemplate = messagingTemplate;
        this.service = service;
    }

    @MessageMapping("/private-message")
    public void handlePrivateMessage(@Payload Chat_private_entity msg) {
        if (msg.getReceiver() == null || msg.getReceiver().isBlank()) return;
        service.sendAndSave(msg);
    }

    @GetMapping("/api/history")
    @ResponseBody
    public List<Chat_private_entity> getHistory(
            @RequestParam String user1,
            @RequestParam String user2) {
        return service.getHistory(user1, user2);
    }

    @MessageMapping("/private-edit")
    public void editPrivateMessage(@Payload Map<String, String> payload) {
        try {
            Long id = Long.parseLong(payload.get("id"));
            String content = payload.get("content");
            String sender = payload.get("sender");

            // كنجيبو الميساج موديفيي كامل من الـ Service
            Chat_private_entity updatedMsg = service.editMessage(id, content, sender);

            // ✅ هنا استعمل updatedMsg.getReceiver() لي جاي من الداتابيز
            messagingTemplate.convertAndSendToUser(updatedMsg.getSender(), "/queue/messages", updatedMsg);
            messagingTemplate.convertAndSendToUser(updatedMsg.getReceiver(), "/queue/messages", updatedMsg);

        } catch (Exception e) {
            System.err.println("Erreur private-edit: " + e.getMessage());
        }
    }

    @MessageMapping("/private-delete")
    public void deletePrivateMessage(@Payload Map<String, String> payload) {
        try {
            Long id = Long.parseLong(payload.get("id"));
            String sender = payload.get("sender");

            // كنجيبو الميساج ممسوح من الـ Service
            Chat_private_entity deletedMsg = service.deleteMessage(id, sender);

            // ✅ نفس الشيء، استعمل المعلومات الحقيقية من الداتابيز
            messagingTemplate.convertAndSendToUser(deletedMsg.getSender(), "/queue/messages", deletedMsg);
            messagingTemplate.convertAndSendToUser(deletedMsg.getReceiver(), "/queue/messages", deletedMsg);

        } catch (Exception e) {
            System.err.println("Erreur private-delete: " + e.getMessage());
        }
    }
}
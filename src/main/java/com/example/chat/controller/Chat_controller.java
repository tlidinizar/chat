package com.example.chat.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


import com.example.chat.entity.Chat_entity;
import com.example.chat.service.Chat_service;
import com.example.chat.websockets.ChatEventListener;




import org.springframework.beans.factory.annotation.Autowired;


@Controller
public class Chat_controller {

	@Autowired
    public final Chat_service chat_service;
	  @Autowired
	private SimpMessagingTemplate messagingTemplate; 

   
    public Chat_controller(Chat_service chat_service) {
    	this.chat_service = chat_service;
    }
    
    @MessageMapping("/sendMessage")
    public void processMessage(@Payload Chat_entity message) {

        // 1. تأكيد الوقت
        message.setTimestamp(LocalDateTime.now());
        
        // 2. تأكيد الـ Type إيلا وصل ناقص من الـ JS
        if (message.getType() == null) {
            message.setType(utils.Utils.MessageType.CHAT);
        }

        // 3. حفظ الرسالة فـ Database
        Chat_entity savedMsg = chat_service.saveMessage(message);
        
        // 4. الإرسال للـ Global Chat
        // تأكد أن JavaScript داير subscribe لـ /topic/messages
        messagingTemplate.convertAndSend("/topic/messages", savedMsg);
        
        System.out.println("Message Global envoyé par: " + savedMsg.getSender());
    }

    @MessageMapping("/getOnlineUsers")
    public void getOnlineUsers() {
        // إرسال قائمة المتصلين
        messagingTemplate.convertAndSend("/topic/online-users", ChatEventListener.onlineUsers.values());
    }

    @MessageMapping("/editMessage")
    public void editMessage(@Payload Map<String, String> payload) {
        try {
            Long id = Long.parseLong(payload.get("id"));
            String content = payload.get("content");
            String sender = payload.get("sender");
            Chat_entity updatedMsg = chat_service.editMessage(id, content, sender);
            messagingTemplate.convertAndSend("/topic/messages", updatedMsg);
        } catch (Exception e) {
            System.err.println("Erreur editMessage: " + e.getMessage());
        }
    }

    @MessageMapping("/deleteMessage")
    public void deleteMessage(@Payload Map<String, String> payload) {
        try {
            Long id = Long.parseLong(payload.get("id"));
            String sender = payload.get("sender");
            Chat_entity deletedMsg = chat_service.deleteMessage(id, sender);
            messagingTemplate.convertAndSend("/topic/messages", deletedMsg);
        } catch (Exception e) {
            System.err.println("Erreur deleteMessage: " + e.getMessage());
        }
    }


}


    
    
    


    

    
    
    
    
    



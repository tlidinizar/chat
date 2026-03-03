package com.example.chat.controller;

import com.example.chat.entity.Chat_entity;
import com.example.chat.service.Chat_service;
import com.example.chat.websockets.ChatEventListener;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;



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
        message.setTimestamp(LocalDateTime.now());
        Chat_entity savedMsg = chat_service.saveMessage(message);
        
        // إرسال يدوي واحد ومحدد
        messagingTemplate.convertAndSend("/topic/messages", savedMsg);
    }
 // Controller.java
    @MessageMapping("/getOnlineUsers")
    public void getOnlineUsers() {
        // كنعيطو لـ onlineUsers بالسمية ديال الكلاس فين كاينة
        messagingTemplate.convertAndSend("/topic/online-users", ChatEventListener.onlineUsers.values());
    }
   /*
    @MessageMapping("/sendMessage") 
    @SendTo("/topic/messages")      
    public Chat_entity processMessage(Chat_entity message) {
        message.setTimestamp(LocalDateTime.now());  
        return chat_service.saveMessage(message);     
    }*/
   
} 
  

    
    
    


    

    
    
    
    
    



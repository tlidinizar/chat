package com.example.chat.service;


import com.example.chat.entity.Chat_entity;
import com.example.chat.repository.Chat_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class Chat_service {

	    @Autowired
	    private Chat_repository chat_repository;

	    public Chat_entity saveMessage(Chat_entity message) {
	        message.setTimestamp(LocalDateTime.now());
	        
	        if (message.getContent() == null || message.getContent().isEmpty()) {
	            throw new RuntimeException("Message vide impossible ");
	        }

	        return chat_repository.save(message);
	    }

	    public List<Chat_entity> getAllMessages() {
	        return chat_repository.findAll();
	    }
}
	    
	    //
	    
	 










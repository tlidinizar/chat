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
	    
	    public Chat_entity editMessage(Long id, String newContent, String username) {
	        Chat_entity message = chat_repository.findById(id).orElseThrow(() -> new RuntimeException("Message non trouvé"));
	        if (!message.getSender().equals(username)) {
	            throw new RuntimeException("Non autorisé");
	        }
	        if (message.getTimestamp().plusMinutes(20).isBefore(LocalDateTime.now())) {
	            throw new RuntimeException("Délai de 20 minutes dépassé");
	        }
	        message.setContent(newContent);
	        message.setEdited(true);
	        return chat_repository.save(message);
	    }

	    public Chat_entity deleteMessage(Long id, String username) {
	        Chat_entity message = chat_repository.findById(id).orElseThrow(() -> new RuntimeException("Message non trouvé"));
	        if (!message.getSender().equals(username)) {
	            throw new RuntimeException("Non autorisé");
	        }
	        
	        message.setDeleted(true);
	        message.setContent(" Ce message a été supprimé");
	        return chat_repository.save(message);
	    }

}
	    //
	    
	 




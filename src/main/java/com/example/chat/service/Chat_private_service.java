package com.example.chat.service;

import com.example.chat.entity.Chat_entity;
import com.example.chat.entity.Chat_private_entity;
import com.example.chat.repository.Block_repository;
import com.example.chat.repository.Chat_private_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class Chat_private_service {

    @Autowired
    private Chat_private_repository chat_private_repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private Block_repository blockRepository;

    public void sendAndSave(Chat_private_entity msg) {
        msg.setTimestamp(LocalDateTime.now());
        Chat_private_entity saved = chat_private_repository.save(msg);

        System.out.println(" Envoi à: [" + saved.getReceiver() + "]");

        messagingTemplate.convertAndSendToUser(
            saved.getReceiver(),
            "/queue/messages",
            saved
        
        );
        messagingTemplate.convertAndSendToUser(
        		saved.getSender(),
        		"/queue/messages",
        		saved
        		);

        // ✅ Renvoyer le message à l'expéditeur avec son id serveur
        // Nécessaire pour les vocaux/fichiers afin que le frontend puisse les afficher et les gérer par id
      //  if (saved.getAudioUrl() != null || saved.getFileUrl() != null) {
          //  messagingTemplate.convertAndSendToUser(
           //     saved.getSender(),
           //    "/queue/messages",
           //     saved
         //   );
      //  }

        String notif = "Nouveau message de " + saved.getFirstName() + " " + saved.getLastName()
                     + "|" + saved.getSender();
        messagingTemplate.convertAndSendToUser(
            saved.getReceiver(),
            "/queue/notifications",
            notif
        );
        
    }

    public List<Chat_private_entity> getHistory(String user1, String user2) {
        return chat_private_repository.findConversation(user1,  user2);
    }
    
    public Chat_private_entity editMessage(Long id, String newContent, String username) {
        Chat_private_entity message = chat_private_repository.findById(id).orElseThrow(() -> new RuntimeException("Message non trouvé"));
        if (!message.getSender().equals(username)) {
            throw new RuntimeException("Non autorisé");
        }
        if (message.getTimestamp().plusMinutes(20).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Délai de 20 minutes dépassé");
        }
        message.setContent(newContent);
        message.setEdited(true);
        return chat_private_repository.save(message);
    }

    public Chat_private_entity deleteMessage(Long id, String username) {
        Chat_private_entity message = chat_private_repository.findById(id).orElseThrow(() -> new RuntimeException("Message non trouvé"));
        if (!message.getSender().equals(username)) {
            throw new RuntimeException("Non autorisé");
        }
        
        message.setDeleted(true);
        message.setContent(" Ce message a été supprimé");
        return chat_private_repository.save(message);
    }
}
package com.example.chat.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ChatEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;

    // Key: Email (Unique identifier), Value: Full Name (Display name)
    public static final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    public ChatEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        // كنجبدو الهيدرز اللي صيفطتي من الـ JS
        var loginHeader = sha.getNativeHeader("login");
        var firstNameHeader = sha.getNativeHeader("firstName");
        var lastNameHeader = sha.getNativeHeader("lastName");

        if (loginHeader != null && firstNameHeader != null && lastNameHeader != null) {
            String email = loginHeader.get(0);
            String fullName = firstNameHeader.get(0).trim() + " " + lastNameHeader.get(0).trim();

            // كنخزنو المستخدم بـ الإيميل ديالو
            onlineUsers.put(email, fullName);

            // كنخزنو الإيميل فـ السيسيون باش ينفعنا فـ الـ Disconnect
            if (sha.getSessionAttributes() != null) {
                sha.getSessionAttributes().put("userEmail", email);
            }

            logger.info("User connected: {} ({})", fullName, email);
            
            // كنصيفطو غير "السميات" لجميع المستخدمين
            messagingTemplate.convertAndSend("/topic/online-users", new ArrayList<>(onlineUsers.values()));
        }
    }

    @EventListener
    public void handleSessionDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());

        // كنجبدو الإيميل اللي خبينا فـ الـ SessionAttributes
        String email = (String) (sha.getSessionAttributes() != null ? sha.getSessionAttributes().get("userEmail") : null);

        if (email != null) {
            String removedUser = onlineUsers.remove(email);
            logger.info("User disconnected: {} ({})", removedUser, email);

            // تحديث القائمة عند كلشي en temps réel
            messagingTemplate.convertAndSend("/topic/online-users", new ArrayList<>(onlineUsers.values()));
        }
    }
}
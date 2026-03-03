package com.example.chat.websockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ChatEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;

    // استعملنا Map باش نربطو الإيميل بالسمية الكاملة
    public static final Map<String, String> onlineUsers = new ConcurrentHashMap<>();

    public ChatEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        
        var loginHeader = sha.getNativeHeader("login");
        var firstNameHeader = sha.getNativeHeader("firstName");
        var lastNameHeader = sha.getNativeHeader("lastName");

        if (loginHeader != null && firstNameHeader != null && lastNameHeader != null) {
            String email = loginHeader.get(0);
            String fullName = firstNameHeader.get(0) + " " + lastNameHeader.get(0);
            
            // كنخزنو المستخدم
            onlineUsers.put(email, fullName);
            
            
            
            // ضروري نخزنو الإيميل فـ الـ SessionAttributes باش ينفعنا فـ الـ Disconnect
            if (sha.getSessionAttributes() != null) {
                sha.getSessionAttributes().put("userEmail", email);
            }

            logger.info("User connected: {} ({})", fullName, email);
            
            // كنصيفطو غير "السميات" (values) للـ Frontend
            messagingTemplate.convertAndSend("/topic/online-users", onlineUsers.values());
        }
    }

    @EventListener
    public void handleSessionDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        
        // كنجبدو الإيميل اللي خبينا فـ الـ Session
        String email = (String) (sha.getSessionAttributes() != null ? sha.getSessionAttributes().get("userEmail") : null);
        
        if (email != null) {
            String fullName = onlineUsers.remove(email);
            logger.info("User disconnected: {}", fullName);
            
            // تحديث القائمة عند كلشي
            messagingTemplate.convertAndSend("/topic/online-users", onlineUsers.values());
        }
    }
}
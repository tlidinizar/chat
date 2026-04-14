package com.example.chat.websockets;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;

public class UserHandshakeHandler extends DefaultHandshakeHandler {

	@Override
	protected Principal determineUser(ServerHttpRequest request,
	                                  WebSocketHandler wsHandler,
	                                  Map<String, Object> attributes) {
	    // ✅ D'abord cherche dans attributes (mis par l'interceptor)
	    Object nameAttr = attributes.get("name");
	    if (nameAttr != null) {
	        System.out.println("✅ PRINCIPAL: " + nameAttr);
	        return new StompPrincipal(nameAttr.toString());
	    }

	    // Fallback query string
	    String query = request.getURI().getQuery();
	    System.out.println("🔵 QUERY: " + query);
	    if (query != null) {
	        for (String param : query.split("&")) {
	            if (param.startsWith("name=")) {
	                String name = URLDecoder.decode(
	                    param.substring(5), StandardCharsets.UTF_8);
	                System.out.println("✅ PRINCIPAL from query: " + name);
	                return new StompPrincipal(name);
	            }
	        }
	    }
	    System.out.println("❌ PRINCIPAL NULL");
	    return null;
	}
}
package com.example.chat.controller;

import com.example.chat.entity.Users_entity;
import com.example.chat.service.Users_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/profile")
public class Profil_controller {
    
    @Autowired
    private Users_service users_service;
    
    /**
     * جلب معلومات المستخدم
     * GET /profile/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        try {
            Optional<Users_entity> userOpt = users_service.getUserById(id);
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur non trouvé"));
            }
            
            Users_entity user = userOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("firstName", user.getFirstname());
            response.put("lastName", user.getLastname());
            response.put("email", user.getMail());
            response.put("avatarUrl", user.getAvatar_url());
            response.put("password", user.getPassword());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur serveur: " + e.getMessage()));
        }
    }
    
    /**
     * تحديث جزئي - PATCH (تحديث فقط الحقول اللي جات)
     * PATCH /profile/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdate(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Users_entity updatedUser = users_service.partialUpdate(id, updates);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profil mis à jour avec succès");
            response.put("id", updatedUser.getId());
            response.put("firstName", updatedUser.getFirstname());
            response.put("lastName", updatedUser.getLastname());
            response.put("email", updatedUser.getMail());
            response.put("avatarUrl", updatedUser.getAvatar_url());
            response.put("password", updatedUser.getPassword());

            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
   
    @PatchMapping("/{id}/avatar")
    public ResponseEntity<?> updateAvatar(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            String avatarUrl = payload.get("avatarUrl");
            if (avatarUrl == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "L'URL de l'avatar est requise"));
            }
            
            Users_entity updatedUser = users_service.updateAvatar(id, avatarUrl);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Avatar mis à jour avec succès");
            response.put("avatarUrl", updatedUser.getAvatar_url());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
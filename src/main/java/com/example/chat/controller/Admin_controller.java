package com.example.chat.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.chat.service.Users_service;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Admin_controller {

    @Autowired
    private Users_service users_service;

    @PutMapping("/promote/{id}")
    public ResponseEntity<String> promote(@PathVariable Long id) {
        users_service.promoteToAdmin(id);
        return ResponseEntity.ok("User " + id + " promoted to ADMIN");
    }

    @PutMapping("/authorize/{id}")
    public ResponseEntity<String> authorize(@PathVariable Long id) {
        users_service.authorizeUser(id);
        return ResponseEntity.ok("User " + id + " activated");
    }

    @DeleteMapping("/refuse/{id}")
    public ResponseEntity<String> refuse(@PathVariable Long id) {
        users_service.refuseUser(id);
        return ResponseEntity.ok("User " + id + " deleted");
    }

    @GetMapping("/pending")
    public ResponseEntity<?> pendingUsers() {
        return ResponseEntity.ok(users_service.getPendingUsers());
    }
   
}
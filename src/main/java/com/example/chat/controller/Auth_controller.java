package com.example.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.chat.entity.Users_entity;
import com.example.chat.service.Users_service;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")  
public class Auth_controller {

	    @Autowired
	    private Users_service users_service;

	    @PostMapping("/register")
	    public String register(@RequestBody Users_entity user) {
	        return users_service.register(user);
	    }

	    @PostMapping("/verify")
	    public String verify(@RequestParam String email, @RequestParam String code) {
	        return users_service.verifyAccount(email, code);
	    }
	    
	  
	    
	    @PostMapping("/login")
	    public ResponseEntity<?> login(@RequestBody Users_entity user) {
	        Users_entity authenticatedUser = users_service.findByMailAndPassword(user.getMail(), user.getPassword());
	        
	        if (authenticatedUser == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error");
	        }

	        // ✅ زيد هاد الشرط
	        if (!authenticatedUser.isEnabled()) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("PENDING_APPROVAL");
	        }

	        return ResponseEntity.ok(authenticatedUser);
	    } 
}   
	
	    
	    
	    
	    
	    
	

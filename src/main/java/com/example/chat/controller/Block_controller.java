package com.example.chat.controller;

import com.example.chat.entity.Block_entity;
import com.example.chat.repository.Block_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/block")
@CrossOrigin("*")
public class Block_controller {

    @Autowired
    private Block_repository blockRepository;

    @PostMapping
    public ResponseEntity<?> block(@RequestParam String blocker,
                                   @RequestParam String blocked) {
        if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
            return ResponseEntity.ok(Map.of("message", "Déjà bloqué"));
        }
        Block_entity b = new Block_entity();
        b.setBlocker(blocker);
        b.setBlocked(blocked);
        blockRepository.save(b);
        return ResponseEntity.ok(Map.of("message", "Utilisateur bloqué"));
    }
    @DeleteMapping
    public ResponseEntity<?> unblock(@RequestParam String blocker,
                                     @RequestParam String blocked) {
        blockRepository.findByBlockerAndBlocked(blocker, blocked)
                .ifPresent(blockRepository::delete);
        return ResponseEntity.ok(Map.of("message", "Utilisateur débloqué"));
    }
    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam String blocker,
                                   @RequestParam String blocked) {
        boolean isBlocked = blockRepository.existsByBlockerAndBlocked(blocker, blocked);
        return ResponseEntity.ok(Map.of("blocked", isBlocked));
    }
}
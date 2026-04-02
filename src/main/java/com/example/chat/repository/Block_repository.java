
package com.example.chat.repository;

import com.example.chat.entity.Block_entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface Block_repository extends JpaRepository<Block_entity, Long> {
    Optional<Block_entity> findByBlockerAndBlocked(String blocker, String blocked);
    boolean existsByBlockerAndBlocked(String blocker, String blocked);
}
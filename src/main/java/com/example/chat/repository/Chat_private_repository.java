package com.example.chat.repository;

import com.example.chat.entity.Chat_private_entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Chat_private_repository extends JpaRepository<Chat_private_entity, Long> {

    @Query("SELECT m FROM Chat_private_entity m WHERE " +
           "(m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1) " +
           "ORDER BY m.timestamp ASC")
    List<Chat_private_entity> findConversation(@Param("user1") String user1,
                                               @Param("user2") String user2);
}
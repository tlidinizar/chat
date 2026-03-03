package com.example.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chat.entity.Chat_entity;

@Repository
public interface Chat_repository extends JpaRepository<Chat_entity, Long>{

	List<Chat_entity> findBySenderAndReceiverOrReceiverAndSender(String sender, String receiver, String sender2,
			String receiver2);

	
	

}

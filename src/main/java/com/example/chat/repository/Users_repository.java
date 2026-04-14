package com.example.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.chat.entity.Users_entity;





@Repository
public interface Users_repository extends JpaRepository<Users_entity, Long> {
	
	
	

	Optional<Users_entity> findByFirstnameAndLastname(String firstname, String lastname);


	Optional<Users_entity> findByFirstnameAndLastnameAndPassword(String fn, String ln, String ps);



	Optional<Users_entity> findByMail(String email);


	Users_entity findByMailAndPassword(String mail, String password);


	List<Users_entity> findByEnabledFalse();


}
package com.example.chat.entity;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Users_entity {
	
   

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @Column(unique = true)
    public String lastname;
    
    public String firstname;
    
    public String password;
    
    public String confirme_password;
    
    public String mail;
    
    public String verification_Code;
    
    public String role = "USER";
    		
    private boolean enabled;
    
    
		

    public boolean isEnabled() {
        return enabled;	
    }

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	




	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirme_password() {
		return confirme_password;
	}

	public void setConfirme_password(String confirme_password) {
		this.confirme_password = confirme_password;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getVerification_Code() {
		return verification_Code;
	}

	public void setVerification_Code(String verification_Code) {
		this.verification_Code = verification_Code;
	}




	
	
    
    
	
	
    
}
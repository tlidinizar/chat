package com.example.chat.service;

import com.example.chat.entity.Users_entity;
import com.example.chat.repository.Users_repository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class Users_service {

    @Autowired
    private Users_repository users_repository;

    @Autowired
    private JavaMailSender mailSender; 

    public String register(Users_entity user) {

        if (users_repository.findByFirstnameAndLastname(user.getFirstname(), user.getLastname()).isPresent()) {
            return "Error: This Full Name is already registered!";
        }

        if (!user.getPassword().equals(user.getConfirme_password())) {
            return "Passwords do not match!";
        }

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        if (!user.getPassword().matches(passwordRegex)) {
            return "Password  Need Maj, Min, and Numbers.";
        }

        if (users_repository.findByMail(user.getMail()).isPresent()) {
            return "Email already exists!";
        }

        String code = String.valueOf((int)((Math.random() * 900000) + 100000));
        user.setVerification_Code(code);

        try {
            sendEmail(user.getMail(), code);
            users_repository.save(user);
            return "SUCCESS: Code sent to your email. Please verify!";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }

    private void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verification Code");
        message.setText("Your code is: " + code);
        mailSender.send(message);
    }

    public String verifyAccount(String email, String code) {
        return users_repository.findByMail(email)
            .map(u -> {
                if (u.getVerification_Code().equals(code)) {
                    u.setEnabled(false); // false = en attente d'approbation admin
                    users_repository.save(u);
                    return "Account Verified Successfully!";
                }
                return "Invalid Code!";
            }).orElse("User not found!");
    }

    public boolean checkSimpleLogin(String email, String password) {
        Optional<Users_entity> userOpt = users_repository.findByMail(email);
        if (userOpt.isPresent()) {
            Users_entity user = userOpt.get();
            return user.getPassword().equals(password);
        }
        return false;
    }

    public Users_entity findByMailAndPassword(String mail, String password) {
        return users_repository.findByMailAndPassword(mail, password);
    }

    public Users_entity findById(Long id) {
        return users_repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'id : " + id));
    }

    @Transactional
    public void authorizeUser(Long id) {
        Users_entity user = users_repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setEnabled(true); // ✅ CORRECTION : true pour activer le compte
        users_repository.save(user);
    }

    public List<Users_entity> getPendingUsers() {
        return users_repository.findByEnabledFalse(); // ✅ CORRECTION : sans underscores
    }

    @Transactional
    public Users_entity promoteToAdmin(Long id) {
        Users_entity user = users_repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setRole("ADMIN");
        user.setEnabled(true);
        return users_repository.save(user);
    }

    @Transactional
    public void refuseUser(Long id) {
        Users_entity user = users_repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        users_repository.delete(user);
    }

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    /**
     * جلب مستخدم بالـ ID
     */
    public Optional<Users_entity> getUserById(Long id) {
        return users_repository.findById(id);
    }
    
    /**
     * جلب مستخدم بالإيميل
     */
    public Optional<Users_entity> getUserByEmail(String email) {
        return users_repository.findByMail(email);
    }
    
    /**
     * تحديث جزئي - فقط الحقول اللي موجودة فـ Map
     * المستخدم يقدر يحدّث حقل واحد أو عدة حقول بدون ما يملأ الكل
     */
    @Transactional
    public Users_entity partialUpdate(Long userId, Map<String, Object> updates) throws Exception {
        Users_entity user = users_repository.findById(userId)
            .orElseThrow(() -> new Exception("Utilisateur non trouvé"));
        
        // تحديث الاسم الشخصي
        if (updates.containsKey("firstName")) {
            String firstName = (String) updates.get("firstName");
            if (firstName != null && !firstName.trim().isEmpty()) {
                user.setFirstname(firstName.trim());
            }
        }
        
        // تحديث اسم العائلة
        if (updates.containsKey("lastName")) {
            String lastName = (String) updates.get("lastName");
            if (lastName != null && !lastName.trim().isEmpty()) {
                user.setLastname(lastName.trim());
            }
        }
        
        // تحديث الإيميل (مع التحقق من عدم التكرار)
        if (updates.containsKey("email")) {
            String email = (String) updates.get("email");
            if (email != null && !email.trim().isEmpty()) {
                if (!user.getMail().equals(email)) {
                    Optional<Users_entity> existingUser = users_repository.findByMail(email);
                    if (existingUser.isPresent()) {
                        throw new Exception("Cet email est déjà utilisé");
                    }
                }
                user.setMail(email.trim());
            }
        }
        
        // تحديث كلمة السر (بدون تشفير - عادية)
        if (updates.containsKey("password")) {
            String password = (String) updates.get("password");
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password.trim());
            }
        }
        
        // تحديث الصورة
        if (updates.containsKey("avatarUrl")) {
            String avatarUrl = (String) updates.get("avatarUrl");
            user.setAvatar_url(avatarUrl != null ? avatarUrl.trim() : null);
        }
        
        return users_repository.save(user);
    }
    
    /**
     * تحديث الصورة فقط
     */
    @Transactional
    public Users_entity updateAvatar(Long userId, String avatarUrl) throws Exception {
        Users_entity user = users_repository.findById(userId)
            .orElseThrow(() -> new Exception("Utilisateur non trouvé"));
        
        user.setAvatar_url(avatarUrl);
        return users_repository.save(user);
    }
    
    /**
     * التحقق إذا كان الإيميل موجود
     */
    public Optional<Users_entity> mailExists(String mail) {
        return users_repository.findByMail(mail);
    }
}

        
        
        
        
        
        
        
      
        
        
        
        
    



package com.example.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "blocks")
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Block_entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String blocker;  
    private String blocked;  

    public Long getId() { return id; }
    public String getBlocker() { return blocker; }
    public void setBlocker(String blocker) { this.blocker = blocker; }
    public String getBlocked() { return blocked; }
    public void setBlocked(String blocked) { this.blocked = blocked; }
}
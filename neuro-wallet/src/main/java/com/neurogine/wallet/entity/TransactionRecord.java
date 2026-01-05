package com.neurogine.wallet.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long senderId;
    private Long receiverId;
    private Double amount;
    private LocalDateTime timestamp;
}
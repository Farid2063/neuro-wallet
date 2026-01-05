package com.neurogine.wallet.repository;
import com.neurogine.wallet.entity.TransactionRecord;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
    List<TransactionRecord> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
}
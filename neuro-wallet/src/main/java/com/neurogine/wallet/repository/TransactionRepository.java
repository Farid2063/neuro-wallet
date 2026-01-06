package com.neurogine.wallet.repository;

import com.neurogine.wallet.entity.TransactionRecord;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
    List<TransactionRecord> findBySenderIdOrReceiverId(Long senderId, Long receiverId);
}
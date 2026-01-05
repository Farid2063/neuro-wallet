package com.neurogine.wallet.repository;
import com.neurogine.wallet.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {
}

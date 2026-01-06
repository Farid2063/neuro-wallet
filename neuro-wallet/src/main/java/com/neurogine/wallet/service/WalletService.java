package com.neurogine.wallet.service;

import com.neurogine.wallet.entity.Wallet;
import com.neurogine.wallet.entity.TransactionRecord;
import com.neurogine.wallet.repository.WalletRepository;
import com.neurogine.wallet.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public String transfer(Long fromId, Long toId, Double amount) {
        Wallet sender = walletRepository.findByUserId(fromId);
        Wallet receiver = walletRepository.findByUserId(toId);

        if (sender.getBalance() < amount) {
            return "Failed: Insufficient balance";
        }

        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        walletRepository.save(sender);
        walletRepository.save(receiver);

        // Create and save transaction record
        TransactionRecord transaction = new TransactionRecord();
        transaction.setSenderId(fromId);
        transaction.setReceiverId(toId);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return "Success";
    }
}

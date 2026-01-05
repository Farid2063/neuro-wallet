package com.neurogine.wallet.controller;

import com.neurogine.wallet.dto.TransferRequest;
import com.neurogine.wallet.entity.TransactionRecord;
import com.neurogine.wallet.repository.TransactionRepository;
import com.neurogine.wallet.service.WalletService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*") // Allows your Vue 3 frontend to connect
public class WalletController {
    @Autowired private WalletService walletService;
    @Autowired private TransactionRepository transactionRepository;

    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request) {
        return walletService.transfer(request.getFromWalletId(), request.getToWalletId(), request.getAmount());
    }

    @GetMapping("/history/{userId}")
    public List<TransactionRecord> getTransactionHistory(@PathVariable Long userId) {
        return transactionRepository.findBySenderIdOrReceiverId(userId, userId);
    }
}
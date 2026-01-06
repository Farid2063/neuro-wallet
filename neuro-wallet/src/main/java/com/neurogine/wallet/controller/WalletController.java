package com.neurogine.wallet.controller;

import com.neurogine.wallet.dto.TransferRequest;
import com.neurogine.wallet.entity.TransactionRecord;
import com.neurogine.wallet.repository.TransactionRepository;
import com.neurogine.wallet.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "http://localhost:5173")
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired private WalletService walletService;
    @Autowired private TransactionRepository transactionRepository;

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        logger.info("Transfer Request: From User {} to User {} Amount RM {}", 
                   request.getFromUserId(), request.getToUserId(), request.getAmount());
        try {
            String result = walletService.transfer(request.getFromUserId(), request.getToUserId(), request.getAmount());
            logger.info("Transfer Successful: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Transfer Failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Transfer failed: " + e.getMessage());
        }
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransactionRecord>> getTransactionHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) String search) {
        
        logger.info("Fetching transaction history for user: {}", userId);
        try {
            List<TransactionRecord> history = transactionRepository.findBySenderIdOrReceiverId(userId, userId);
            
            // Search/Filter Logic
            if (search != null && !search.isEmpty()) {
                history = history.stream()
                    .filter(t -> t.getAmount().toString().contains(search))
                    .collect(Collectors.toList());
            }
            
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Error fetching history: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
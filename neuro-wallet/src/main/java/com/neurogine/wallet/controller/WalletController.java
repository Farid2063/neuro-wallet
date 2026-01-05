package com.neurogine.wallet.controller;

import com.neurogine.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*") // Allows your Vue 3 frontend to connect
public class WalletController {
    @Autowired private WalletService walletService;

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long from, @RequestParam Long to, @RequestParam Double amount) {
        return walletService.transfer(from, to, amount);
    }
}
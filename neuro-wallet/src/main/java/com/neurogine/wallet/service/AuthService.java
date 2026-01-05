package com.neurogine.wallet.service;

import com.neurogine.wallet.entity.User;
import com.neurogine.wallet.entity.Wallet;
import com.neurogine.wallet.repository.UserRepository;
import com.neurogine.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired private UserRepository userRepo;
    @Autowired private WalletRepository walletRepo;

    public User registerUser(String username, String password) {
        if (userRepo.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // In production, hash the password!
        User savedUser = userRepo.save(newUser);

        // Create a wallet for the new user
        Wallet wallet = new Wallet();
        wallet.setUserId(savedUser.getId());
        wallet.setBalance(0.0);
        walletRepo.save(wallet);

        return savedUser;
    }

    public User login(String username, String password) {
        return userRepo.findByUsername(username)
                .filter(user -> user.getPassword().equals(password)) // In production, verify hashed password!
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
    }
}

package com.neurogine.wallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neurogine.wallet.entity.User;
import com.neurogine.wallet.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("* ")
public class AuthController {
    @Autowired private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // temporary: return OK until AuthService.register(User) is implemented
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        return ResponseEntity.ok(authService.login(user.getUsername(), user.getPassword()));
    }
}

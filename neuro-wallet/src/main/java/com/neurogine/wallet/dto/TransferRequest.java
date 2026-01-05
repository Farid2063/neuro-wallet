package com.neurogine.wallet.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private Long fromWalletId; // Renamed from fromUserId
    private Long toWalletId;   // Renamed from toUserId
    private Double amount;
}
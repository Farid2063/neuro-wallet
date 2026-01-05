package com.neurogine.wallet.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private Long fromUserId;
    private Long toUserId;
    private Double amount;
}
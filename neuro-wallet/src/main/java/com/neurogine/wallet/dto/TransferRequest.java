package com.neurogine.wallet.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private Long fromWalletId; // Renamed from fromUserId
    private Long toWalletId;   // Renamed from toUserId
    private Double amount;

    public Long getFromUserId() {return fromWalletId;}
    public void setFromUserId(Long fromUserId) {this.fromWalletId = fromUserId;}

    public Long getToUserId() {return toWalletId;}
    public void setToUserId(Long toUserId) {this.toWalletId = toUserId;}

    public Double getAmount() {return amount;}
    public void setAmount(Double amount) {this.amount = amount;}
}
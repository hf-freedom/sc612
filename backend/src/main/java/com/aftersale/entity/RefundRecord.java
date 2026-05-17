package com.aftersale.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RefundRecord {
    private String refundId;
    private String afterSaleId;
    private String orderId;
    private double amount;
    private String status;
    private LocalDateTime createTime;
}

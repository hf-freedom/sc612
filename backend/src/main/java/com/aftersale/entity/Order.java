package com.aftersale.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Order {
    private String orderId;
    private String userId;
    private String productId;
    private String productName;
    private int quantity;
    private double price;
    private LocalDateTime createTime;
    private String status;
    private boolean isExpired;
    private long remainingDays;
}

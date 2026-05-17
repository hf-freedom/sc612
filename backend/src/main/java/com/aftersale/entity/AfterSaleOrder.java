package com.aftersale.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AfterSaleOrder {
    private String afterSaleId;
    private String orderId;
    private String userId;
    private String productId;
    private String productName;
    private String reason;
    private String description;
    private String type;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String currentNode;
    private String inspector;
    private String inspectionResult;
    private String repairPerson;
    private LocalDateTime repairDeadline;
    private LocalDateTime refundTime;
    private String newProductId;
    private String shipmentId;
    private Integer upgradeLevel = 0;
}

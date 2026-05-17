package com.aftersale.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShipmentTask {
    private String taskId;
    private String afterSaleId;
    private String type;
    private String address;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime receiveTime;
}

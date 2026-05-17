package com.aftersale.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpgradeRecord {
    private String recordId;
    private String afterSaleId;
    private String oldRepairPerson;
    private String newRepairPerson;
    private Integer upgradeLevel;
    private LocalDateTime upgradeTime;
    private String reason;
}

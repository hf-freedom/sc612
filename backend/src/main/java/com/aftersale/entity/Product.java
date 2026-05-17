package com.aftersale.entity;

import lombok.Data;

@Data
public class Product {
    private String productId;
    private String productName;
    private int stock;
    private double price;
    private String category;
    private boolean supportReturn;
    private boolean supportExchange;
    private boolean supportRepair;
}

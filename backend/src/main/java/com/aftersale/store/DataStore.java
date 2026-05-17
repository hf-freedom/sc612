package com.aftersale.store;

import com.aftersale.entity.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    public static final Map<String, Order> ORDERS = new ConcurrentHashMap<>();
    public static final Map<String, AfterSaleOrder> AFTER_SALE_ORDERS = new ConcurrentHashMap<>();
    public static final Map<String, Product> PRODUCTS = new ConcurrentHashMap<>();
    public static final Map<String, ShipmentTask> SHIPMENT_TASKS = new ConcurrentHashMap<>();
    public static final Map<String, RefundRecord> REFUND_RECORDS = new ConcurrentHashMap<>();
    public static final Map<String, List<UpgradeRecord>> UPGRADE_RECORDS = new ConcurrentHashMap<>();
    public static final List<String> REPAIR_PERSONS = new ArrayList<>(Arrays.asList("张工", "李工", "王工"));

    static {
        initData();
    }

    private static void initData() {
        Product p1 = new Product();
        p1.setProductId("P001");
        p1.setProductName("智能手机");
        p1.setStock(100);
        p1.setPrice(2999.0);
        p1.setCategory("电子产品");
        p1.setSupportReturn(true);
        p1.setSupportExchange(true);
        p1.setSupportRepair(true);
        PRODUCTS.put(p1.getProductId(), p1);

        Product p2 = new Product();
        p2.setProductId("P002");
        p2.setProductName("笔记本电脑");
        p2.setStock(50);
        p2.setPrice(5999.0);
        p2.setCategory("电子产品");
        p2.setSupportReturn(true);
        p2.setSupportExchange(true);
        p2.setSupportRepair(true);
        PRODUCTS.put(p2.getProductId(), p2);

        Product p3 = new Product();
        p3.setProductId("P003");
        p3.setProductName("定制礼品");
        p3.setStock(200);
        p3.setPrice(299.0);
        p3.setCategory("礼品");
        p3.setSupportReturn(false);
        p3.setSupportExchange(true);
        p3.setSupportRepair(false);
        PRODUCTS.put(p3.getProductId(), p3);

        Order o1 = new Order();
        o1.setOrderId("O001");
        o1.setUserId("U001");
        o1.setProductId("P001");
        o1.setProductName("智能手机");
        o1.setQuantity(1);
        o1.setPrice(2999.0);
        o1.setCreateTime(LocalDateTime.now().minusDays(15));
        o1.setStatus("已完成");
        ORDERS.put(o1.getOrderId(), o1);

        Order o2 = new Order();
        o2.setOrderId("O002");
        o2.setUserId("U002");
        o2.setProductId("P002");
        o2.setProductName("笔记本电脑");
        o2.setQuantity(1);
        o2.setPrice(5999.0);
        o2.setCreateTime(LocalDateTime.now().minusDays(10));
        o2.setStatus("已完成");
        ORDERS.put(o2.getOrderId(), o2);

        Order o3 = new Order();
        o3.setOrderId("O003");
        o3.setUserId("U001");
        o3.setProductId("P003");
        o3.setProductName("定制礼品");
        o3.setQuantity(2);
        o3.setPrice(598.0);
        o3.setCreateTime(LocalDateTime.now().minusDays(40));
        o3.setStatus("已完成");
        ORDERS.put(o3.getOrderId(), o3);
    }
}

package com.aftersale.service;

import com.aftersale.entity.*;
import com.aftersale.store.DataStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AfterSaleService {

    @Value("${aftersale.warranty-period-days:30}")
    private int warrantyPeriodDays;

    @Value("${aftersale.repair-timeout-days:3}")
    private int repairTimeoutDays;

    public Map<String, Object> createAfterSale(String orderId, String reason, String description, String type) {
        Map<String, Object> result = new HashMap<>();

        Order order = DataStore.ORDERS.get(orderId);
        if (order == null) {
            result.put("success", false);
            result.put("message", "订单不存在");
            return result;
        }

        long daysBetween = ChronoUnit.DAYS.between(order.getCreateTime(), LocalDateTime.now());
        if (daysBetween > warrantyPeriodDays) {
            result.put("success", false);
            result.put("message", "订单已超过" + warrantyPeriodDays + "天售后期");
            return result;
        }

        Product product = DataStore.PRODUCTS.get(order.getProductId());
        if (product == null) {
            result.put("success", false);
            result.put("message", "商品不存在");
            return result;
        }

        if ("退款".equals(type) && !product.isSupportReturn()) {
            result.put("success", false);
            result.put("message", "该商品不支持退款");
            return result;
        }
        if ("换货".equals(type) && !product.isSupportExchange()) {
            result.put("success", false);
            result.put("message", "该商品不支持换货");
            return result;
        }
        if ("维修".equals(type) && !product.isSupportRepair()) {
            result.put("success", false);
            result.put("message", "该商品不支持维修");
            return result;
        }

        AfterSaleOrder afterSale = new AfterSaleOrder();
        afterSale.setAfterSaleId("AS" + System.currentTimeMillis());
        afterSale.setOrderId(orderId);
        afterSale.setUserId(order.getUserId());
        afterSale.setProductId(order.getProductId());
        afterSale.setProductName(order.getProductName());
        afterSale.setReason(reason);
        afterSale.setDescription(description);
        afterSale.setType(type);
        afterSale.setStatus("待审核");
        afterSale.setCurrentNode("待审核");
        afterSale.setCreateTime(LocalDateTime.now());
        afterSale.setUpdateTime(LocalDateTime.now());

        DataStore.AFTER_SALE_ORDERS.put(afterSale.getAfterSaleId(), afterSale);

        result.put("success", true);
        result.put("message", "售后申请提交成功");
        result.put("data", afterSale);
        return result;
    }

    public Map<String, Object> approveAfterSale(String afterSaleId) {
        Map<String, Object> result = new HashMap<>();

        AfterSaleOrder afterSale = DataStore.AFTER_SALE_ORDERS.get(afterSaleId);
        if (afterSale == null) {
            result.put("success", false);
            result.put("message", "售后单不存在");
            return result;
        }

        afterSale.setStatus("审核通过");
        afterSale.setCurrentNode("待用户寄回");
        afterSale.setUpdateTime(LocalDateTime.now());

        ShipmentTask task = new ShipmentTask();
        task.setTaskId("ST" + System.currentTimeMillis());
        task.setAfterSaleId(afterSaleId);
        task.setType("用户寄回");
        task.setStatus("待发货");
        task.setAddress("北京市朝阳区售后仓库");
        task.setCreateTime(LocalDateTime.now());

        DataStore.SHIPMENT_TASKS.put(task.getTaskId(), task);

        result.put("success", true);
        result.put("message", "审核通过，已生成寄回任务");
        result.put("data", afterSale);
        return result;
    }

    public Map<String, Object> rejectAfterSale(String afterSaleId, String reason) {
        Map<String, Object> result = new HashMap<>();

        AfterSaleOrder afterSale = DataStore.AFTER_SALE_ORDERS.get(afterSaleId);
        if (afterSale == null) {
            result.put("success", false);
            result.put("message", "售后单不存在");
            return result;
        }

        afterSale.setStatus("已拒绝");
        afterSale.setCurrentNode("已结束");
        afterSale.setUpdateTime(LocalDateTime.now());

        result.put("success", true);
        result.put("message", "审核已拒绝");
        result.put("data", afterSale);
        return result;
    }

    public Map<String, Object> confirmReceive(String afterSaleId) {
        Map<String, Object> result = new HashMap<>();

        AfterSaleOrder afterSale = DataStore.AFTER_SALE_ORDERS.get(afterSaleId);
        if (afterSale == null) {
            result.put("success", false);
            result.put("message", "售后单不存在");
            return result;
        }

        afterSale.setCurrentNode("待质检");
        afterSale.setUpdateTime(LocalDateTime.now());

        result.put("success", true);
        result.put("message", "仓库已确认收货");
        result.put("data", afterSale);
        return result;
    }

    public Map<String, Object> submitInspection(String afterSaleId, String inspector, String resultText) {
        Map<String, Object> result = new HashMap<>();

        AfterSaleOrder afterSale = DataStore.AFTER_SALE_ORDERS.get(afterSaleId);
        if (afterSale == null) {
            result.put("success", false);
            result.put("message", "售后单不存在");
            return result;
        }

        afterSale.setInspector(inspector);
        afterSale.setInspectionResult(resultText);
        afterSale.setUpdateTime(LocalDateTime.now());

        if ("退款".equals(afterSale.getType())) {
            afterSale.setCurrentNode("待退款");
            RefundRecord refund = new RefundRecord();
            refund.setRefundId("RF" + System.currentTimeMillis());
            refund.setAfterSaleId(afterSaleId);
            refund.setOrderId(afterSale.getOrderId());
            Order order = DataStore.ORDERS.get(afterSale.getOrderId());
            refund.setAmount(order != null ? order.getPrice() : 0);
            refund.setStatus("待退款");
            refund.setCreateTime(LocalDateTime.now());
            DataStore.REFUND_RECORDS.put(refund.getRefundId(), refund);
            result.put("message", "已生成待退款记录");
        } else if ("换货".equals(afterSale.getType())) {
            afterSale.setCurrentNode("待发货");
            Product product = DataStore.PRODUCTS.get(afterSale.getProductId());
            if (product != null && product.getStock() > 0) {
                product.setStock(product.getStock() - 1);
                afterSale.setNewProductId(product.getProductId());
                ShipmentTask task = new ShipmentTask();
                task.setTaskId("SH" + System.currentTimeMillis());
                task.setAfterSaleId(afterSaleId);
                task.setType("换货发货");
                task.setStatus("待发货");
                task.setCreateTime(LocalDateTime.now());
                DataStore.SHIPMENT_TASKS.put(task.getTaskId(), task);
                result.put("message", "已锁定库存并生成发货任务");
            } else {
                result.put("success", false);
                result.put("message", "库存不足");
                return result;
            }
        } else if ("维修".equals(afterSale.getType())) {
            afterSale.setCurrentNode("维修中");
            int idx = new Random().nextInt(DataStore.REPAIR_PERSONS.size());
            afterSale.setRepairPerson(DataStore.REPAIR_PERSONS.get(idx));
            afterSale.setRepairDeadline(LocalDateTime.now().plusDays(repairTimeoutDays));
            result.put("message", "已分配维修人员：" + afterSale.getRepairPerson());
        }

        result.put("success", true);
        result.put("data", afterSale);
        return result;
    }

    public Map<String, Object> confirmRefund(String refundId) {
        Map<String, Object> result = new HashMap<>();

        RefundRecord refund = DataStore.REFUND_RECORDS.get(refundId);
        if (refund == null) {
            result.put("success", false);
            result.put("message", "退款记录不存在");
            return result;
        }

        refund.setStatus("已退款");

        AfterSaleOrder afterSale = DataStore.AFTER_SALE_ORDERS.get(refund.getAfterSaleId());
        if (afterSale != null) {
            afterSale.setStatus("已完成");
            afterSale.setCurrentNode("已结束");
            afterSale.setRefundTime(LocalDateTime.now());
            afterSale.setUpdateTime(LocalDateTime.now());
        }

        result.put("success", true);
        result.put("message", "退款已确认");
        result.put("data", refund);
        return result;
    }

    public Map<String, Object> completeRepair(String afterSaleId) {
        Map<String, Object> result = new HashMap<>();

        AfterSaleOrder afterSale = DataStore.AFTER_SALE_ORDERS.get(afterSaleId);
        if (afterSale == null) {
            result.put("success", false);
            result.put("message", "售后单不存在");
            return result;
        }

        afterSale.setStatus("已完成");
        afterSale.setCurrentNode("已结束");
        afterSale.setUpdateTime(LocalDateTime.now());

        result.put("success", true);
        result.put("message", "维修已完成");
        result.put("data", afterSale);
        return result;
    }

    public Map<String, Object> confirmShipment(String taskId, String logisticsCompany, String trackingNumber) {
        Map<String, Object> result = new HashMap<>();

        ShipmentTask task = DataStore.SHIPMENT_TASKS.get(taskId);
        if (task == null) {
            result.put("success", false);
            result.put("message", "发货任务不存在");
            return result;
        }

        task.setReceiveTime(LocalDateTime.now());

        AfterSaleOrder afterSale = DataStore.AFTER_SALE_ORDERS.get(task.getAfterSaleId());
        if (afterSale != null) {
            afterSale.setStatus("已完成");
            afterSale.setCurrentNode("已结束");
            afterSale.setUpdateTime(LocalDateTime.now());
        }

        result.put("success", true);
        result.put("message", "发货确认成功");
        result.put("data", task);
        return result;
    }

    public List<AfterSaleOrder> getAllAfterSales() {
        return new ArrayList<>(DataStore.AFTER_SALE_ORDERS.values());
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>(DataStore.ORDERS.values());
        for (Order order : orders) {
            long daysBetween = ChronoUnit.DAYS.between(order.getCreateTime(), LocalDateTime.now());
            long remaining = warrantyPeriodDays - daysBetween;
            order.setExpired(remaining <= 0);
            order.setRemainingDays(remaining);
        }
        return orders;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(DataStore.PRODUCTS.values());
    }

    public List<ShipmentTask> getAllShipmentTasks() {
        return new ArrayList<>(DataStore.SHIPMENT_TASKS.values());
    }

    public List<RefundRecord> getAllRefundRecords() {
        return new ArrayList<>(DataStore.REFUND_RECORDS.values());
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        Map<String, Integer> reasonCount = new HashMap<>();
        long totalDuration = 0;
        int completedCount = 0;
        int refundCount = 0;
        int exchangeCount = 0;

        for (AfterSaleOrder order : DataStore.AFTER_SALE_ORDERS.values()) {
            reasonCount.merge(order.getReason(), 1, Integer::sum);

            if ("已完成".equals(order.getStatus()) && order.getUpdateTime() != null) {
                long duration = ChronoUnit.HOURS.between(order.getCreateTime(), order.getUpdateTime());
                totalDuration += duration;
                completedCount++;
            }

            if ("退款".equals(order.getType()) && "已完成".equals(order.getStatus())) {
                refundCount++;
            }
            if ("换货".equals(order.getType()) && "已完成".equals(order.getStatus())) {
                exchangeCount++;
            }
        }

        stats.put("reasonStatistics", reasonCount);
        stats.put("avgProcessingHours", completedCount > 0 ? totalDuration / completedCount : 0);
        stats.put("refundCount", refundCount);
        stats.put("exchangeCount", exchangeCount);
        int totalCompleted = refundCount + exchangeCount;
        stats.put("refundRate", totalCompleted > 0 ? (refundCount * 100.0 / totalCompleted) : 0);
        stats.put("exchangeRate", totalCompleted > 0 ? (exchangeCount * 100.0 / totalCompleted) : 0);
        stats.put("totalAfterSales", DataStore.AFTER_SALE_ORDERS.size());

        return stats;
    }

    public List<UpgradeRecord> getUpgradeRecords(String afterSaleId) {
        return DataStore.UPGRADE_RECORDS.getOrDefault(afterSaleId, new ArrayList<>());
    }

    public Map<String, Object> triggerUpgrade(String afterSaleId) {
        Map<String, Object> result = new HashMap<>();
        
        AfterSaleOrder order = DataStore.AFTER_SALE_ORDERS.get(afterSaleId);
        if (order == null || !"维修中".equals(order.getCurrentNode())) {
            result.put("success", false);
            result.put("message", "售后单不存在或不在维修中");
            return result;
        }

        String currentPerson = order.getRepairPerson();
        int idx = DataStore.REPAIR_PERSONS.indexOf(currentPerson != null ? currentPerson.replace("(主管)", "") : "");
        String nextPerson;
        if (idx >= 0 && idx < DataStore.REPAIR_PERSONS.size() - 1) {
            nextPerson = DataStore.REPAIR_PERSONS.get(idx + 1);
        } else {
            nextPerson = DataStore.REPAIR_PERSONS.get(0) + "(主管)";
        }
        
        int newLevel = (order.getUpgradeLevel() != null ? order.getUpgradeLevel() + 1 : 1);
        order.setUpgradeLevel(newLevel);
        order.setRepairPerson(nextPerson);
        order.setRepairDeadline(LocalDateTime.now().plusDays(3));
        
        UpgradeRecord record = new UpgradeRecord();
        record.setRecordId("UR" + System.currentTimeMillis());
        record.setAfterSaleId(afterSaleId);
        record.setOldRepairPerson(currentPerson);
        record.setNewRepairPerson(nextPerson);
        record.setUpgradeLevel(newLevel);
        record.setUpgradeTime(LocalDateTime.now());
        record.setReason("手动升级");
        
        List<UpgradeRecord> records = DataStore.UPGRADE_RECORDS.getOrDefault(afterSaleId, new ArrayList<>());
        records.add(record);
        DataStore.UPGRADE_RECORDS.put(afterSaleId, records);
        
        result.put("success", true);
        result.put("message", "升级成功");
        result.put("data", record);
        return result;
    }
}

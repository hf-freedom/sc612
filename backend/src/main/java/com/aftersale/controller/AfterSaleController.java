package com.aftersale.controller;

import com.aftersale.entity.*;
import com.aftersale.service.AfterSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aftersale")
@CrossOrigin(origins = "*")
public class AfterSaleController {

    @Autowired
    private AfterSaleService afterSaleService;

    @PostMapping("/create")
    public Map<String, Object> createAfterSale(@RequestBody Map<String, String> params) {
        return afterSaleService.createAfterSale(
                params.get("orderId"),
                params.get("reason"),
                params.get("description"),
                params.get("type")
        );
    }

    @PostMapping("/approve/{afterSaleId}")
    public Map<String, Object> approveAfterSale(@PathVariable String afterSaleId) {
        return afterSaleService.approveAfterSale(afterSaleId);
    }

    @PostMapping("/reject/{afterSaleId}")
    public Map<String, Object> rejectAfterSale(@PathVariable String afterSaleId, @RequestBody Map<String, String> params) {
        return afterSaleService.rejectAfterSale(afterSaleId, params.get("reason"));
    }

    @PostMapping("/receive/{afterSaleId}")
    public Map<String, Object> confirmReceive(@PathVariable String afterSaleId) {
        return afterSaleService.confirmReceive(afterSaleId);
    }

    @PostMapping("/inspection/{afterSaleId}")
    public Map<String, Object> submitInspection(@PathVariable String afterSaleId, @RequestBody Map<String, String> params) {
        return afterSaleService.submitInspection(afterSaleId, params.get("inspector"), params.get("result"));
    }

    @PostMapping("/refund/{refundId}")
    public Map<String, Object> confirmRefund(@PathVariable String refundId) {
        return afterSaleService.confirmRefund(refundId);
    }

    @PostMapping("/complete-repair/{afterSaleId}")
    public Map<String, Object> completeRepair(@PathVariable String afterSaleId) {
        return afterSaleService.completeRepair(afterSaleId);
    }

    @GetMapping("/upgrade-records/{afterSaleId}")
    public List<UpgradeRecord> getUpgradeRecords(@PathVariable String afterSaleId) {
        return afterSaleService.getUpgradeRecords(afterSaleId);
    }

    @PostMapping("/trigger-upgrade/{afterSaleId}")
    public Map<String, Object> triggerUpgrade(@PathVariable String afterSaleId) {
        return afterSaleService.triggerUpgrade(afterSaleId);
    }

    @PostMapping("/confirm-shipment/{taskId}")
    public Map<String, Object> confirmShipment(@PathVariable String taskId, @RequestBody Map<String, String> params) {
        return afterSaleService.confirmShipment(taskId, params.get("logisticsCompany"), params.get("trackingNumber"));
    }

    @GetMapping("/list")
    public List<AfterSaleOrder> getAllAfterSales() {
        return afterSaleService.getAllAfterSales();
    }

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return afterSaleService.getAllOrders();
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return afterSaleService.getAllProducts();
    }

    @GetMapping("/shipments")
    public List<ShipmentTask> getAllShipmentTasks() {
        return afterSaleService.getAllShipmentTasks();
    }

    @GetMapping("/refunds")
    public List<RefundRecord> getAllRefundRecords() {
        return afterSaleService.getAllRefundRecords();
    }

    @GetMapping("/statistics")
    public Map<String, Object> getStatistics() {
        return afterSaleService.getStatistics();
    }
}

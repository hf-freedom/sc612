package com.aftersale.task;

import com.aftersale.entity.AfterSaleOrder;
import com.aftersale.entity.UpgradeRecord;
import com.aftersale.store.DataStore;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RepairTimeoutTask {

    @Scheduled(cron = "0 0 * * * ?")
    public void checkRepairTimeout() {
        for (AfterSaleOrder order : DataStore.AFTER_SALE_ORDERS.values()) {
            if ("维修中".equals(order.getCurrentNode()) && order.getRepairDeadline() != null) {
                if (LocalDateTime.now().isAfter(order.getRepairDeadline())) {
                    String currentPerson = order.getRepairPerson();
                    String nextPerson = getNextRepairPerson(currentPerson);
                    
                    int newLevel = (order.getUpgradeLevel() != null ? order.getUpgradeLevel() + 1 : 1);
                    order.setUpgradeLevel(newLevel);
                    order.setRepairPerson(nextPerson);
                    order.setRepairDeadline(LocalDateTime.now().plusDays(3));
                    
                    UpgradeRecord record = new UpgradeRecord();
                    record.setRecordId("UR" + System.currentTimeMillis());
                    record.setAfterSaleId(order.getAfterSaleId());
                    record.setOldRepairPerson(currentPerson);
                    record.setNewRepairPerson(nextPerson);
                    record.setUpgradeLevel(newLevel);
                    record.setUpgradeTime(LocalDateTime.now());
                    record.setReason("维修超时自动升级");
                    
                    List<UpgradeRecord> records = DataStore.UPGRADE_RECORDS.getOrDefault(order.getAfterSaleId(), new ArrayList<>());
                    records.add(record);
                    DataStore.UPGRADE_RECORDS.put(order.getAfterSaleId(), records);
                    
                    System.out.println("维修超时，已将负责人从 " + currentPerson + " 升级为 " + nextPerson + "，升级级别：" + newLevel);
                }
            }
        }
    }

    private String getNextRepairPerson(String current) {
        if (current == null) {
            return DataStore.REPAIR_PERSONS.get(0);
        }
        int idx = DataStore.REPAIR_PERSONS.indexOf(current.replace("(主管)", ""));
        if (idx >= 0 && idx < DataStore.REPAIR_PERSONS.size() - 1) {
            return DataStore.REPAIR_PERSONS.get(idx + 1);
        }
        return DataStore.REPAIR_PERSONS.get(0) + "(主管)";
    }
}

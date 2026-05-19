package com.infotact.wms.wms.controller;

import com.infotact.wms.wms.entity.InventoryItem;
import com.infotact.wms.wms.service.InventoryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")

public class InventoryItemController {

    @Autowired
    private InventoryItemService inventoryItemService;

    @PostMapping("/receive")
    public InventoryItem recieveItem(@RequestParam Long productId,@RequestParam Long binId,@RequestParam String serialNumber){
        return inventoryItemService.recieveItem(productId,binId,serialNumber);

    }
    @PostMapping("/ship/{itemId}")
    public InventoryItem shipItem(@PathVariable Long itemId){
        return inventoryItemService.shipItem(itemId);
    }



    @GetMapping("/filter/status")
    public List<InventoryItem> getItemsByStatus(@RequestParam String status){
        return inventoryItemService.getItemsByStatus(status);
    }

    @GetMapping("/filter/sku")
    public List<InventoryItem> getItemsBySku(@RequestParam String sku){
        return inventoryItemService.getItemsBySku(sku);
    }

    @GetMapping("/warehouse/{warehouseId}")
    public List<InventoryItem> getItemsByWarehouse(@PathVariable Long warehouseId){
        return inventoryItemService.getItemsByWarehouse(warehouseId);
    }



}

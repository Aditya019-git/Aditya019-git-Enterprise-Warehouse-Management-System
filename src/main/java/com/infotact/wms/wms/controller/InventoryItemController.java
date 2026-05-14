package com.infotact.wms.wms.controller;

import com.infotact.wms.wms.entity.InventoryItem;
import com.infotact.wms.wms.service.InventoryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")

public class InventoryItemController {

    @Autowired
    private InventoryItemService inventoryItemService;

    @PostMapping("/receive")
    public InventoryItem recieveItem(@RequestParam Long productId,@RequestParam Long binId,@RequestParam String serialNumber){
        return inventoryItemService.recieveItem(productId,binId,serialNumber);

    }



}

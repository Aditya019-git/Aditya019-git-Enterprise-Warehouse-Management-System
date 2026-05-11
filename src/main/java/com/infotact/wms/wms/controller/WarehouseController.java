package com.infotact.wms.wms.controller;


import com.infotact.wms.wms.entity.Warehouse;
import com.infotact.wms.wms.repository.WarehouseRepository;
import com.infotact.wms.wms.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    @Autowired
    private WarehouseService warehouseService;

    @GetMapping
    public List<Warehouse> getAllWarehouses(){
        return warehouseService.getAllWarehouses();
    }

    @PostMapping
    public ResponseEntity<Warehouse> createWarehouse(@RequestBody Warehouse warehouse){
        Warehouse savedWarehouse=warehouseService.saveWarehouse(warehouse);
        return ResponseEntity.ok(savedWarehouse);
    }

}

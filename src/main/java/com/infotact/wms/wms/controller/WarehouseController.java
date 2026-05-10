package com.infotact.wms.wms.controller;


import com.infotact.wms.wms.entity.Warehouse;
import com.infotact.wms.wms.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    @Autowired
    private WarehouseRepository warehouseRepository;

    @GetMapping
    public List<Warehouse> getAllWarehouses(){
        return warehouseRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Warehouse> createWarehouse(@RequestBody Warehouse warehouse){
        Warehouse savedWarehouse=warehouseRepository.save(warehouse);
        return ResponseEntity.ok(savedWarehouse);
    }

}

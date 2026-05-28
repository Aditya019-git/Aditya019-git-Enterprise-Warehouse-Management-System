package com.infotact.wms.wms.controller;


import com.infotact.wms.wms.entity.Warehouse;
import com.infotact.wms.wms.repository.WarehouseRepository;
import com.infotact.wms.wms.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    @Autowired
    private WarehouseService warehouseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public List<Warehouse> getAllWarehouses(){
        return warehouseService.getAllWarehouses();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Warehouse> createWarehouse(@Valid @RequestBody Warehouse warehouse){
        Warehouse savedWarehouse=warehouseService.saveWarehouse(warehouse);
        return ResponseEntity.ok(savedWarehouse);
    }

}

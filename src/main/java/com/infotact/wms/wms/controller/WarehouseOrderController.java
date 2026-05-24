package com.infotact.wms.wms.controller;


import com.infotact.wms.wms.entity.OrderStatus;
import com.infotact.wms.wms.entity.WarehouseOrder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class WarehouseOrderController {

    @Autowired
    private WarehouseOrderService warehouseOrderService;

    @PostMapping
    public ResponseEntity<WarehouseOrder> createOrder(@Valid @RequestBody WarehouseOrder order){
        return ResponseEntity.ok(warehouseOrderService.createOrder(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseOrder> getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(warehouseOrderService.getOrderById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<WarehouseOrder> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status){
        return ResponseEntity.ok(warehouseOrderService.updateOrderStatus(id,status));
    }
}

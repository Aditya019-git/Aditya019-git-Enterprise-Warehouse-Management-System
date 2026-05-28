package com.infotact.wms.wms.controller;


import java.util.List;
import com.infotact.wms.wms.entity.OrderStatus;
import com.infotact.wms.wms.entity.WarehouseOrder;
import com.infotact.wms.wms.service.WarehouseOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class WarehouseOrderController {

    @Autowired
    private WarehouseOrderService warehouseOrderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<WarehouseOrder> createOrder(@Valid @RequestBody WarehouseOrder order){
        return ResponseEntity.ok(warehouseOrderService.createOrder(order));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<List<WarehouseOrder>> getAllOrders(){
        return ResponseEntity.ok(warehouseOrderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<WarehouseOrder> getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(warehouseOrderService.getOrderById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<WarehouseOrder> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status){
        return ResponseEntity.ok(warehouseOrderService.updateOrderStatus(id,status));
    }
}

package com.infotact.wms.wms.service;


import com.infotact.wms.wms.entity.*;
import com.infotact.wms.wms.exception.InsufficientStockException;
import com.infotact.wms.wms.exception.InvalidInventoryStateException;
import com.infotact.wms.wms.exception.ResourceNotFoundException;
import com.infotact.wms.wms.repository.InventoryItemRepository;
import com.infotact.wms.wms.repository.ProductRepository;
import com.infotact.wms.wms.repository.StorageBinRepository;
import com.infotact.wms.wms.repository.WarehouseOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WarehouseOrderService {

    @Autowired
    private WarehouseOrderRepository warehouseOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private StorageBinRepository storageBinRepository;

    @Transactional
    public WarehouseOrder createOrder(WarehouseOrder order){
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        for(OrderItem item:order.getOrderItems()){
            Product product=productRepository.findById(item.getProduct().getId()).orElseThrow(()->new ResourceNotFoundException("Product not Found with id: "+item.getProduct().getId()));
            item.setProduct(product);
            item.setOrder(order);

        }
        return warehouseOrderRepository.save(order);
    }

    @Transactional
    public WarehouseOrder updateOrderStatus(Long orderId, OrderStatus newStatus){
        WarehouseOrder order = warehouseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        OrderStatus currentStatus = order.getStatus();
        // 1. Enforce State Machine rules
        boolean isValidTransaction = false;
        if(currentStatus == newStatus){
            isValidTransaction = true;
        } else if(currentStatus == OrderStatus.PENDING && newStatus == OrderStatus.PICKING){
            isValidTransaction = true;
        } else if(currentStatus == OrderStatus.PICKING && newStatus == OrderStatus.PACKED){
            isValidTransaction = true;
        } else if (currentStatus == OrderStatus.PACKED && newStatus == OrderStatus.SHIPPED) {
            isValidTransaction = true;
        }
        if(!isValidTransaction){
            throw new InvalidInventoryStateException("Illegal order transition: Cannot change status from " + currentStatus + " to " + newStatus);
        }
        // 2. Fulfill & Ship Inventory Logic (Triggered on transition to SHIPPED)
        if (newStatus == OrderStatus.SHIPPED) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                int orderedQty = orderItem.getQuantity();
                // Fetch all active "Available" stock items for this product
                List<InventoryItem> availableItems = inventoryItemRepository
                        .findByProductIdAndStatusIgnoreCase(product.getId(), "Available");
                // Stock Check Validation
                if (availableItems.size() < orderedQty) {
                    throw new InsufficientStockException("Insufficient stock to ship product: "
                            + product.getName() + " (SKU: " + product.getSku() + "). "
                            + "Ordered: " + orderedQty + ", Available: " + availableItems.size());
                }
                // Pick and deduct inventory items one by one
                for (int i = 0; i < orderedQty; i++) {
                    InventoryItem item = availableItems.get(i);
                    item.setStatus("SHIPPED");
                    StorageBin bin = item.getStorageBin();
                    if (bin != null) {
                        // Free shelf capacity
                        int newOccupancy = bin.getCurrentOccupancy() - 1;
                        bin.setCurrentOccupancy(Math.max(0, newOccupancy));
                        storageBinRepository.save(bin);
                    }

                    // Disassociate item from physical shelf
                    item.setStorageBin(null);
                    inventoryItemRepository.save(item);
                }
            }
        }
        order.setStatus(newStatus);
        return warehouseOrderRepository.save(order);
    }

    public List<WarehouseOrder> getAllOrders(){
        return warehouseOrderRepository.findAll();
    }

    public WarehouseOrder getOrderById(Long id){
        return warehouseOrderRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Order not found with id: "+id));
    }
}

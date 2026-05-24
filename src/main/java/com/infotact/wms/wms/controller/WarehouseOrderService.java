package com.infotact.wms.wms.controller;


import com.infotact.wms.wms.entity.OrderItem;
import com.infotact.wms.wms.entity.OrderStatus;
import com.infotact.wms.wms.entity.Product;
import com.infotact.wms.wms.entity.WarehouseOrder;
import com.infotact.wms.wms.exception.InvalidInventoryStateException;
import com.infotact.wms.wms.exception.ResourceNotFoundException;
import com.infotact.wms.wms.repository.ProductRepository;
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
    public WarehouseOrder updateOrderStatus(Long orderId,OrderStatus newStatus){
        WarehouseOrder order=warehouseOrderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order not found with id: "+orderId));
        OrderStatus currentStatus=order.getStatus();

        boolean isValidTransaction =false;
        if(currentStatus==newStatus){
            isValidTransaction=true;
        }else if(currentStatus==OrderStatus.PENDING && newStatus==OrderStatus.PICKING){
            isValidTransaction=true;
        }else if(currentStatus==OrderStatus.PICKING && newStatus==OrderStatus.PACKED){
            isValidTransaction=true;
        } else if (currentStatus==OrderStatus.PACKED && newStatus==OrderStatus.SHIPPED) {
            isValidTransaction=true;
        }
        if(!isValidTransaction){
            throw new InvalidInventoryStateException("Illegal order transaction:Cannot change status from" +currentStatus+" to "+newStatus);
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

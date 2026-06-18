package com.infotact.wms.wms.service;

import com.infotact.wms.wms.entity.*;
import com.infotact.wms.wms.exception.InsufficientStockException;
import com.infotact.wms.wms.exception.InvalidInventoryStateException;
import com.infotact.wms.wms.exception.ResourceNotFoundException;
import com.infotact.wms.wms.repository.InventoryItemRepository;
import com.infotact.wms.wms.repository.ProductRepository;
import com.infotact.wms.wms.repository.StorageBinRepository;
import com.infotact.wms.wms.repository.WarehouseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarehouseOrderServiceTest {

    @Mock
    private WarehouseOrderRepository warehouseOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private StorageBinRepository storageBinRepository;

    @InjectMocks
    private WarehouseOrderService warehouseOrderService;

    private Product product;
    private WarehouseOrder order;
    private OrderItem orderItem;
    private StorageBin bin;
    private InventoryItem inventoryItem;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setSku("PROD-100");
        product.setName("Test Product");

        orderItem = new OrderItem();
        orderItem.setId(10L);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);

        order = new WarehouseOrder();
        order.setId(100L);
        order.setOrderNumber("ORD-123");
        order.setCustomerName("Globex Corp");
        order.setStatus(OrderStatus.PENDING);
        
        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem);
        order.setOrderItems(items);
        orderItem.setOrder(order);

        bin = new StorageBin();
        bin.setId(20L);
        bin.setBinCode("BIN-A1");
        bin.setMaxCapacity(5);
        bin.setCurrentOccupancy(3);

        inventoryItem = new InventoryItem();
        inventoryItem.setId(30L);
        inventoryItem.setProduct(product);
        inventoryItem.setStorageBin(bin);
        inventoryItem.setStatus("Available");
    }

    @Test
    void testCreateOrder_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseOrderRepository.save(any(WarehouseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WarehouseOrder createdOrder = warehouseOrderService.createOrder(order);

        assertNotNull(createdOrder);
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        assertNotNull(createdOrder.getOrderDate());
        verify(warehouseOrderRepository, times(1)).save(order);
    }

    @Test
    void testUpdateOrderStatus_ValidTransition_PendingToPicking() {
        when(warehouseOrderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(warehouseOrderRepository.save(any(WarehouseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WarehouseOrder updatedOrder = warehouseOrderService.updateOrderStatus(100L, OrderStatus.PICKING);

        assertNotNull(updatedOrder);
        assertEquals(OrderStatus.PICKING, updatedOrder.getStatus());
        verify(warehouseOrderRepository, times(1)).save(order);
    }

    @Test
    void testUpdateOrderStatus_InvalidTransition_ThrowsException() {
        when(warehouseOrderRepository.findById(100L)).thenReturn(Optional.of(order));

        // Attempt invalid state transition directly to SHIPPED (skipping PICKING & PACKED)
        assertThrows(InvalidInventoryStateException.class, () -> {
            warehouseOrderService.updateOrderStatus(100L, OrderStatus.SHIPPED);
        });

        verify(warehouseOrderRepository, never()).save(any(WarehouseOrder.class));
    }

    @Test
    void testUpdateOrderStatus_ShippingDeduction_Success() {
        order.setStatus(OrderStatus.PACKED); // Start in PACKED state to ship
        when(warehouseOrderRepository.findById(100L)).thenReturn(Optional.of(order));
        
        // Mock available inventory items in repository (need 2)
        InventoryItem item2 = new InventoryItem();
        item2.setId(31L);
        item2.setProduct(product);
        item2.setStorageBin(bin);
        item2.setStatus("Available");

        List<InventoryItem> mockStock = new ArrayList<>();
        mockStock.add(inventoryItem);
        mockStock.add(item2);

        when(inventoryItemRepository.findAvailableItemsForFulfillment(1L)).thenReturn(mockStock);
        when(warehouseOrderRepository.save(any(WarehouseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WarehouseOrder shippedOrder = warehouseOrderService.updateOrderStatus(100L, OrderStatus.SHIPPED);

        assertNotNull(shippedOrder);
        assertEquals(OrderStatus.SHIPPED, shippedOrder.getStatus());
        
        // Assert bin occupancy decremented twice (from 3 down to 1)
        assertEquals(1, bin.getCurrentOccupancy());
        
        // Assert inventory status changed to SHIPPED and bin disassociated
        assertEquals("SHIPPED", inventoryItem.getStatus());
        assertNull(inventoryItem.getStorageBin());
        assertEquals("SHIPPED", item2.getStatus());
        assertNull(item2.getStorageBin());

        verify(storageBinRepository, times(2)).save(bin);
        verify(inventoryItemRepository, times(2)).save(any(InventoryItem.class));
        verify(warehouseOrderRepository, times(1)).save(order);
    }

    @Test
    void testUpdateOrderStatus_ShippingDeduction_InsufficientStock_ThrowsException() {
        order.setStatus(OrderStatus.PACKED);
        when(warehouseOrderRepository.findById(100L)).thenReturn(Optional.of(order));
        
        // Mock insufficient stock (only 1 available but 2 are ordered)
        List<InventoryItem> mockStock = new ArrayList<>();
        mockStock.add(inventoryItem);

        when(inventoryItemRepository.findAvailableItemsForFulfillment(1L)).thenReturn(mockStock);

        assertThrows(InsufficientStockException.class, () -> {
            warehouseOrderService.updateOrderStatus(100L, OrderStatus.SHIPPED);
        });

        // Occupancy should remain unchanged (3)
        assertEquals(3, bin.getCurrentOccupancy());
        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
    }
}

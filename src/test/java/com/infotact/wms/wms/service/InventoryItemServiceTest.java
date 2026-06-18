package com.infotact.wms.wms.service;

import com.infotact.wms.wms.entity.InventoryItem;
import com.infotact.wms.wms.entity.Product;
import com.infotact.wms.wms.entity.StorageBin;
import com.infotact.wms.wms.exception.BinFullException;
import com.infotact.wms.wms.exception.InvalidInventoryStateException;
import com.infotact.wms.wms.exception.ResourceNotFoundException;
import com.infotact.wms.wms.repository.InventoryItemRepository;
import com.infotact.wms.wms.repository.ProductRepository;
import com.infotact.wms.wms.repository.StorageBinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryItemServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StorageBinRepository storageBinRepository;

    @InjectMocks
    private InventoryItemService inventoryItemService;

    private Product product;
    private StorageBin bin;
    private InventoryItem item;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setSku("PROD-100");
        product.setName("Test Product");

        bin = new StorageBin();
        bin.setId(2L);
        bin.setBinCode("BIN-A1");
        bin.setMaxCapacity(5);
        bin.setCurrentOccupancy(2);

        item = new InventoryItem();
        item.setId(3L);
        item.setProduct(product);
        item.setStorageBin(bin);
        item.setSerialNumber("SN-12345");
        item.setStatus("Available");
    }

    @Test
    void testReceiveItem_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(storageBinRepository.findById(2L)).thenReturn(Optional.of(bin));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryItem savedItem = inventoryItemService.recieveItem(1L, 2L, "SN-12345");

        assertNotNull(savedItem);
        assertEquals("Available", savedItem.getStatus());
        assertEquals("SN-12345", savedItem.getSerialNumber());
        assertEquals(3, bin.getCurrentOccupancy()); // Occupancy should increment from 2 to 3
        verify(storageBinRepository, times(1)).save(bin);
        verify(inventoryItemRepository, times(1)).save(any(InventoryItem.class));
    }

    @Test
    void testReceiveItem_BinFull_ThrowsException() {
        bin.setCurrentOccupancy(5); // Equal to maxCapacity (5)
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(storageBinRepository.findById(2L)).thenReturn(Optional.of(bin));

        assertThrows(BinFullException.class, () -> {
            inventoryItemService.recieveItem(1L, 2L, "SN-12345");
        });

        assertEquals(5, bin.getCurrentOccupancy()); // Should not increment
        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    void testReceiveItem_ProductNotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            inventoryItemService.recieveItem(1L, 2L, "SN-12345");
        });

        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
    }

    @Test
    void testShipItem_Success() {
        when(inventoryItemRepository.findById(3L)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryItem shippedItem = inventoryItemService.shipItem(3L);

        assertNotNull(shippedItem);
        assertEquals("SHIPPED", shippedItem.getStatus());
        assertNull(shippedItem.getStorageBin());
        assertEquals(1, bin.getCurrentOccupancy()); // Occupancy should decrement from 2 to 1
        verify(storageBinRepository, times(1)).save(bin);
        verify(inventoryItemRepository, times(1)).save(item);
    }

    @Test
    void testShipItem_NotAvailable_ThrowsException() {
        item.setStatus("SHIPPED");
        when(inventoryItemRepository.findById(3L)).thenReturn(Optional.of(item));

        assertThrows(InvalidInventoryStateException.class, () -> {
            inventoryItemService.shipItem(3L);
        });

        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
    }
}

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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryItemService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StorageBinRepository storageBinRepository;

    @Transactional
    public InventoryItem recieveItem(Long productId, Long binId, String serialNumber) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        StorageBin bin = storageBinRepository.findById(binId)
                .orElseThrow(() -> new ResourceNotFoundException("Storage Bin not found with id: " + binId));

        if (bin.getCurrentOccupancy() >= bin.getMaxCapacity()) {
            throw new BinFullException("Bin " + bin.getBinCode() + " is full (Capacity: " + bin.getMaxCapacity() + ").");
        }

        InventoryItem item = new InventoryItem();
        item.setProduct(product);
        item.setStorageBin(bin);
        item.setSerialNumber(serialNumber);
        item.setStatus("Available");
        item.setDataRecieved(LocalDateTime.now());

        bin.setCurrentOccupancy((bin.getCurrentOccupancy() + 1));
        storageBinRepository.save(bin);

        return inventoryItemRepository.save(item);
    }

    @Transactional
    public InventoryItem shipItem(Long itemId) {
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory Item not found with id: " + itemId));

        if (!"AVAILABLE".equalsIgnoreCase(item.getStatus())) {
            throw new InvalidInventoryStateException("Item is not available for shipping. Current status: " + item.getStatus());
        }

        StorageBin bin = item.getStorageBin();
        if (bin != null) {
            int newOccupency = bin.getCurrentOccupancy() - 1;
            if (newOccupency < 0) {
                newOccupency = 0;
            }
            bin.setCurrentOccupancy(newOccupency);
            storageBinRepository.save(bin);
        }
        item.setStatus("SHIPPED");
        item.setStorageBin(null);

        return inventoryItemRepository.save(item);
    }


    public List<InventoryItem> getItemsBySku(String sku) {
        return inventoryItemRepository.findByProductSku(sku);
    }

    public List<InventoryItem> getItemsByWarehouse(Long warehouseId) {
        return inventoryItemRepository.findByStorageBinWarehouseId(warehouseId);
    }

    public List<InventoryItem> getItemsByStatus(String status) {
        return inventoryItemRepository.findByStatusIgnoreCase(status);
    }

    public List<InventoryItem> getItemsByBin(Long binId) {
        return inventoryItemRepository.findByStorageBinId(binId);
    }
}

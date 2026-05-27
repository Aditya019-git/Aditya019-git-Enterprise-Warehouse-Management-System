package com.infotact.wms.wms.repository;

import com.infotact.wms.wms.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem,Long> {

    // 1. Find all items by their status (e.g., "AVAILABLE", "SHIPPED")
    List<InventoryItem> findByStatusIgnoreCase(String status);

    // 2. Find all items belonging to a product SKU (Nested object query!)
    List<InventoryItem> findByProductSku(String sku);

    // 3. Find all items physically sitting in a specific warehouse
    List<InventoryItem> findByStorageBinWarehouseId(Long warehouseId);

    // 4. Find all items belonging to a product by ID and status
    List<InventoryItem> findByProductIdAndStatusIgnoreCase(Long productId,String status);
}

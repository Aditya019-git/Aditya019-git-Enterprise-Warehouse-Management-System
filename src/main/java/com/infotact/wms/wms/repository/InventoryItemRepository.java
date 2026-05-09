package com.infotact.wms.wms.repository;

import com.infotact.wms.wms.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem,Long> {
}

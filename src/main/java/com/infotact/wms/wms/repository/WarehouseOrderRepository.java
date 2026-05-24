package com.infotact.wms.wms.repository;


import com.infotact.wms.wms.entity.WarehouseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseOrderRepository extends JpaRepository<WarehouseOrder,Long> {
}

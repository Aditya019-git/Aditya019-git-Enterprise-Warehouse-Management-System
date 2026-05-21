package com.infotact.wms.wms.repository;

import com.infotact.wms.wms.entity.StorageBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageBinRepository extends JpaRepository<StorageBin,Long> {

    // JPQL Query to find bins that are not full
    @Query("SELECT b FROM StorageBin b WHERE b.currentOccupancy<b.maxCapacity")
    List<StorageBin> findAvailableBins();
}

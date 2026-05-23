package com.infotact.wms.wms.repository;

import com.infotact.wms.wms.entity.StorageBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StorageBinRepository extends JpaRepository<StorageBin,Long> {

    // JPQL Query to find bins that are not full
    @Query("SELECT b FROM StorageBin b WHERE b.currentOccupancy<b.maxCapacity")
    List<StorageBin> findAvailableBins();

    // Custom query to find a storage bin by its unique label code (e.g. BIN-A1)
    Optional<StorageBin> findByBinCode(String binCode);
}

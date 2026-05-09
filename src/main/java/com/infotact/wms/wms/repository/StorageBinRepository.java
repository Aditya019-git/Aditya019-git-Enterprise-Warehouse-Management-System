package com.infotact.wms.wms.repository;

import com.infotact.wms.wms.entity.StorageBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageBinRepository extends JpaRepository<StorageBin,Long> {
}

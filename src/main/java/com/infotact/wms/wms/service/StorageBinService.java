package com.infotact.wms.wms.service;


import com.infotact.wms.wms.entity.StorageBin;
import com.infotact.wms.wms.repository.StorageBinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StorageBinService {

    @Autowired
    private StorageBinRepository storageBinRepository;

    public StorageBin saveStorageBin(StorageBin bin){
        //Business Rule: A bin cannot have a current occupancy higher than its max capacity
        if(bin.getCurrentOccupancy()>bin.getMaxCapacity()){
            throw new RuntimeException("Overcapacity! Bin "+bin.getBinCode()+"cannot hold more than "+bin.getMaxCapacity()+"items.");

        }
        return storageBinRepository.save(bin);
    }

    public List<StorageBin> getAvailableBins() {
        return storageBinRepository.findAvailableBins();
    }

    public List<StorageBin> getAllBins() {
        return storageBinRepository.findAll();
    }
}

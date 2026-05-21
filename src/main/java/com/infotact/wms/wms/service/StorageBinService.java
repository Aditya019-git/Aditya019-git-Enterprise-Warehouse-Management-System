package com.infotact.wms.wms.service;

import com.infotact.wms.wms.entity.StorageBin;
import com.infotact.wms.wms.exception.InvalidInventoryStateException;
import com.infotact.wms.wms.repository.StorageBinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageBinService {

    @Autowired
    private StorageBinRepository storageBinRepository;

    public StorageBin saveStorageBin(StorageBin bin) {
        if (bin.getCurrentOccupancy() > bin.getMaxCapacity()) {
            throw new InvalidInventoryStateException("Overcapacity! Bin " + bin.getBinCode() + " cannot hold more than " + bin.getMaxCapacity() + " items.");
        }
        return storageBinRepository.save(bin);
    }
}
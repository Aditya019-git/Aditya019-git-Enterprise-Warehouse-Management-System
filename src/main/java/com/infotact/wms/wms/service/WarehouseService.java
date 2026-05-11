package com.infotact.wms.wms.service;

import com.infotact.wms.wms.entity.Warehouse;
import com.infotact.wms.wms.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    public List<Warehouse> getAllWarehouses(){
        return warehouseRepository.findAll();
    }

    public Warehouse saveWarehouse(Warehouse warehouse){
        return warehouseRepository.save(warehouse);
    }

    public Warehouse getWarehouseById(Long id){
        return warehouseRepository.findById(id).orElseThrow(()->new RuntimeException("Warehouse not found with id: " + id));
    }
}

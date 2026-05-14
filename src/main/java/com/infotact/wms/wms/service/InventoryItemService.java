package com.infotact.wms.wms.service;


import com.infotact.wms.wms.entity.InventoryItem;
import com.infotact.wms.wms.entity.Product;
import com.infotact.wms.wms.entity.StorageBin;
import com.infotact.wms.wms.repository.InventoryItemRepository;
import com.infotact.wms.wms.repository.ProductRepository;
import com.infotact.wms.wms.repository.StorageBinRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InventoryItemService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StorageBinRepository storageBinRepository;

    @Transactional
    public InventoryItem recieveItem(Long productId,Long binId,String serialNumber){
        //check if product Exists
        Product product=productRepository.findById(productId).orElseThrow(()-> new RuntimeException("Product not found"));

        //check if Bin exists
        StorageBin  bin =storageBinRepository.findById(binId).orElseThrow(()->new RuntimeException("Storage Bin not Found"));

        //check if bin has space

        if(bin.getCurrentOccupancy()>= bin.getMaxCapacity()){
            throw new RuntimeException("bin is Full");
        }

        //Create the new Item
        InventoryItem item = new InventoryItem();
        item.setProduct(product);
        item.setStorageBin(bin);
        item.setSerialNumber(serialNumber);
        item.setStatus("Available");
        item.setDataRecieved(LocalDateTime.now());

        //Update the bin's occupancy
        bin.setCurrentOccupancy((bin.getCurrentOccupancy()+1));
        storageBinRepository.save(bin);

        //save the item
        return inventoryItemRepository.save(item);

    }

}

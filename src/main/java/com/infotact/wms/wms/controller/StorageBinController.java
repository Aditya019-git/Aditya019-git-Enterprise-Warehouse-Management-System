package com.infotact.wms.wms.controller;


import com.infotact.wms.wms.entity.StorageBin;
import com.infotact.wms.wms.repository.StorageBinRepository;
import com.infotact.wms.wms.service.StorageBinService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bins")
public class StorageBinController {

    @Autowired
    private StorageBinService storageBinService;

    @Autowired
    private StorageBinRepository storageBinRepository;

    @PostMapping
    public ResponseEntity<StorageBin> createBin(@Valid @RequestBody StorageBin bin){
        return ResponseEntity.ok(storageBinService.saveStorageBin(bin));
    }

    @GetMapping("/available")
    public List<StorageBin> getAvailableBins(){
        return storageBinService.getAvailableBins();
    }

    // GET: Retrieve all storage bins currently in the system
    @GetMapping
    public List<StorageBin> getAllBins() {
        // You can call your storageBinRepository.findAll() directly or add a service method!
        return storageBinService.getAllBins();
    }

}

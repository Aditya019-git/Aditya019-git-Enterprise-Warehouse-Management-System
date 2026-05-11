package com.infotact.wms.wms.controller;


import com.infotact.wms.wms.entity.StorageBin;
import com.infotact.wms.wms.service.StorageBinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bins")
public class StorageBinController {

    @Autowired
    private StorageBinService storageBinService;

    @PostMapping
    public ResponseEntity<StorageBin> createBin(@RequestBody StorageBin bin){
        return ResponseEntity.ok(storageBinService.saveStorageBin(bin));
    }
}

package com.infotact.wms.wms.controller;


import com.google.zxing.WriterException;
import com.infotact.wms.wms.entity.Product;
import com.infotact.wms.wms.exception.ResourceNotFoundException;
import com.infotact.wms.wms.repository.ProductRepository;
import com.infotact.wms.wms.service.BarcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/barcodes")
public class BarcodeController {

    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private ProductRepository productRepository;

    //Serves a high-quality printable PNG barcode for a registered product SKU.

    @GetMapping(value="/product/{sku}",produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getProductBarcode(@PathVariable String sku){

        Product product=productRepository.findBySku(sku).orElseThrow(()->new ResourceNotFoundException("Product not Found with sku: "+sku));
        try{
            byte[] imageBytes=barcodeService.generateBarcodeImage(product.getSku(),300,100);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);

        }catch(WriterException| IOException e){
            throw new RuntimeException("Error generating barcode for sku :"+sku,e);
        }
    }


}

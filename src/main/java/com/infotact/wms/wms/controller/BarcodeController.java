package com.infotact.wms.wms.controller;


import com.google.zxing.WriterException;
import com.infotact.wms.wms.entity.Product;
import com.infotact.wms.wms.entity.StorageBin;
import com.infotact.wms.wms.exception.ResourceNotFoundException;
import com.infotact.wms.wms.repository.ProductRepository;
import com.infotact.wms.wms.repository.StorageBinRepository;
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

    @Autowired
    private StorageBinRepository storageBinRepository;

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

    //Returns a JSON containing a Base64-encoded barcode string for a product SKU.
    @GetMapping(value="/product/{sku}/base64",produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BarcodeResponse> getProductBarcodeBase64(@PathVariable String sku){
        Product product=productRepository.findBySku(sku).orElseThrow(()->new ResourceNotFoundException("Product not Found with SKU: "+sku));
        try{
            byte[] imageBytes=barcodeService.generateBarcodeImage(product.getSku(),300,100);
            String base64Image= barcodeService.convertToBase64(imageBytes);

            BarcodeResponse response=new BarcodeResponse(product.getSku(),"CODE_128",base64Image);
            return ResponseEntity.ok(response);
        }catch(WriterException|IOException e){
            throw new RuntimeException("Error generating Base64 barcode for SKU :"+sku,e);
        }
    }

    //Serves a high-quality printable PNG 2D QR Code representing a warehouse storage bin shelf.
    @GetMapping(value="/bin/{id}",produces =MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBinQRCode(@PathVariable Long id){
        StorageBin bin=storageBinRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Storage Bin not found with id: " + id));

        try{
            // Generate standard square QR Code (250x250px)
            byte[] imageBytes=barcodeService.generateQRCodeImage(bin.getBinCode(),250,250);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);

        }catch(WriterException|IOException e){
            throw new RuntimeException("Error generating QR Code for Bin ID: "+id,e);
        }

    }


}

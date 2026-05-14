package com.infotact.wms.wms.controller;

import java.util.*;
import com.infotact.wms.wms.entity.Product;
import com.infotact.wms.wms.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public Product createProduct(@RequestBody Product product){
        return productRepository.save(product);
    }

    @GetMapping
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
}

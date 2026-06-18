package com.infotact.wms.wms.controller;

import java.util.*;
import com.infotact.wms.wms.entity.Product;
import com.infotact.wms.wms.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public Product createProduct(@Valid @RequestBody Product product){
        if (product.getSku() != null && productRepository.findBySku(product.getSku().trim()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU '" + product.getSku().trim() + "' already exists!");
        }
        product.setSku(product.getSku().trim());
        return productRepository.save(product);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
}

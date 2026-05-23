package com.infotact.wms.wms.repository;


import com.infotact.wms.wms.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    // Custom query to find a product by its unique SKU code
    Optional<Product> findBySku(String sku);
}

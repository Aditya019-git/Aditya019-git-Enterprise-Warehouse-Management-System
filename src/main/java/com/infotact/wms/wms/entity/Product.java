package com.infotact.wms.wms.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;  // Unique barcode identifier
    private String name;
    private String description;
    private BigDecimal price;
    private double weight;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<InventoryItem> inventoryItems;

}

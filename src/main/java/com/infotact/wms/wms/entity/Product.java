package com.infotact.wms.wms.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message="SKU is required")
    @Size(min=3,max=20,message="SKU must be between 3 and 20 characters")
    private String sku;  // Unique barcode identifier

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message="Price is required")
    @DecimalMin(value="0.0",inclusive=true,message="Price is cannot be negative")
    private BigDecimal price;

    @Min(value = 0, message = "Weight cannot be negative")
    private double weight;

    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<InventoryItem> inventoryItems;

}

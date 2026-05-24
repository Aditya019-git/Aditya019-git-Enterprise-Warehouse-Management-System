package com.infotact.wms.wms.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="product_id",nullable=false)
    private Product product;

    private Integer quantity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="order_id",nullable=false)
    private WarehouseOrder order;
}

package com.infotact.wms.wms.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String serialNumber;
    private String status;
    private LocalDateTime dataRecieved;

    @ManyToOne
    @JoinColumn(name="storage_bin_id")
    private StorageBin storageBin;

    @ManyToOne
    @JoinColumn(name="product_id",nullable=false)
    private Product product;
}

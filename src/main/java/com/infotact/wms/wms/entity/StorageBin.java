package com.infotact.wms.wms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class StorageBin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String binCode;
    private Integer maxCapacity;
    private Integer currentOccupancy;

    @ManyToOne
    @JoinColumn(name="warehouse_id",nullable=false)
    private Warehouse warehouse;
}

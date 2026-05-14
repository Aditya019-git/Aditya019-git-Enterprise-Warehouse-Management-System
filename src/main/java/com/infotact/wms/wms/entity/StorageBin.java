package com.infotact.wms.wms.entity;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Integer currentOccupancy=0;

    @ManyToOne
    @JoinColumn(name="warehouse_id",nullable=false)
    private Warehouse warehouse;


    @JsonIgnore
    @OneToMany(mappedBy = "storageBin", cascade = CascadeType.ALL)
    private List<InventoryItem> inventoryItems;
}

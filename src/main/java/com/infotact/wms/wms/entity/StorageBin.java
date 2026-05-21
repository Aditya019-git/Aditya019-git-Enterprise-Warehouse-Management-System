package com.infotact.wms.wms.entity;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class StorageBin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Bin code is required")
    private String binCode;

    @NotNull(message = "Max capacity is required")
    @Min(value = 1, message = "Max capacity must be at least 1")
    private Integer maxCapacity;

    private Integer currentOccupancy = 0;

    @ManyToOne
    @JoinColumn(name="warehouse_id", nullable=false)
    private Warehouse warehouse;

    @JsonIgnore
    @OneToMany(mappedBy = "storageBin", cascade = CascadeType.ALL)
    private List<InventoryItem> inventoryItems;
}
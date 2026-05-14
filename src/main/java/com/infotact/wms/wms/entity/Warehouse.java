package com.infotact.wms.wms.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
     private String name;
     private String location;
     private Integer totalCapacity;

    @JsonIgnore
     @OneToMany(mappedBy = "warehouse",cascade = CascadeType.ALL)
     private List<StorageBin> storageBins;

}

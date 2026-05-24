package com.infotact.wms.wms.entity;

public enum OrderStatus {

    PENDING,  // Order received, waiting to be picked
    PICKING,  // Operators are actively locating and gathering items
    PACKED,   // Items are packaged and placed at the shipping dock
    SHIPPED   // Package has left the warehouse
}

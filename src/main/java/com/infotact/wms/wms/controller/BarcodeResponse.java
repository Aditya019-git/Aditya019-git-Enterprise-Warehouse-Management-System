package com.infotact.wms.wms.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarcodeResponse {
    private String value;
    private String name;
    private String base64Image;
}

package com.infotact.wms.wms.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String messagge){
        super(messagge);
    }
}

package com.infotact.wms.wms.exception;

public class InvalidInventoryStateException extends RuntimeException{
    public  InvalidInventoryStateException(String message){
        super(message);
    }
}

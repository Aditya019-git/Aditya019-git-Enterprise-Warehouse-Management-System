package com.infotact.wms.wms.service;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class BarcodeService {
    //Generates a 1D Code 128 barcode image as a byte array.
    public byte[] generateBarcodeImage(String text,int width,int height) throws WriterException, IOException{
        Code128Writer barcodeWriter=new Code128Writer();

        // Encode the text string into a 1D BitMatrix using Code 128 standard
        BitMatrix bitMatrix=barcodeWriter.encode(text, BarcodeFormat.CODE_128,width,height);

        // Convert the BitMatrix into a standard PNG byte array stream
        try(ByteArrayOutputStream pngOutputStream=new ByteArrayOutputStream()){
            MatrixToImageWriter.writeToStream(bitMatrix,"PNG",pngOutputStream);
            return pngOutputStream.toByteArray();
        }
    }

    //Generates a 2D QR Code image as a byte array.
    public byte[] generateQRCodeImage(String text,int width,int height) throws WriterException,IOException{
        QRCodeWriter qrCodeWriter=new QRCodeWriter();

        // Encode the text into a 2D BitMatrix using QR_CODE standard
        BitMatrix bitMatrix=qrCodeWriter.encode(text,BarcodeFormat.QR_CODE,width,height);
        try(ByteArrayOutputStream pngOutputStream=new ByteArrayOutputStream()){
            MatrixToImageWriter.writeToStream(bitMatrix,"PNG",pngOutputStream);
            return pngOutputStream.toByteArray();
        }


    }
    //Converts a binary image byte array into an HTML-compliant Base64 image string
    public String convertToBase64(byte[] imageBytes){
        return "data:image/png;base64,"+ Base64.getEncoder().encodeToString(imageBytes);
    }


}

package com.infotact.wms.wms.service;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class BarcodeService {

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
}

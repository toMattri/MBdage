package it.motta.mbdage.controller;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCodeController {

  public static Bitmap traduceQrCode(String Value,int dim) throws WriterException {
    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
    return barcodeEncoder.encodeBitmap(Value, BarcodeFormat.QR_CODE, dim, dim);

  }

}
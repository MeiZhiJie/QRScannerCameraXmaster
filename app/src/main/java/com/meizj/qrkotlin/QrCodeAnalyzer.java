package com.meizj.qrkotlin;

import android.util.Log;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.android.gms.tasks.Task;

import java.util.List;

interface QrCodesDetected {
    void onQrCodesDetected(List<FirebaseVisionBarcode> codes);
}

public class QrCodeAnalyzer implements ImageAnalysis.Analyzer {
    QrCodesDetected qrCodesDetected;

    public QrCodeAnalyzer(QrCodesDetected qrCodesDetected) {
        this.qrCodesDetected = qrCodesDetected;
    }
    @Override
    public void analyze(ImageProxy image, int rotationDegrees) {
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

        int rotation = rotationDegreesToFirebaseRotation(rotationDegrees);
        FirebaseVisionImage visionImage = FirebaseVisionImage.fromMediaImage(image.getImage(), rotation);

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(visionImage);
        result.addOnSuccessListener(barcodes -> qrCodesDetected.onQrCodesDetected(barcodes))
        .addOnFailureListener(e -> {
            Log.e("read barcode", e.toString());
        });
    }

    private int rotationDegreesToFirebaseRotation(int rotationDegrees) {
        switch (rotationDegrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException("Not supported");
        }
    }
}

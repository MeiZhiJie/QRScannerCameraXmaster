package com.meizj.qrkotlin;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.TextureView;
import android.Manifest;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.camera.core.*;
import androidx.lifecycle.LifecycleOwner;
import android.widget.Toast;

import android.os.Bundle;

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA_PERMISSION = 10;
    private TextureView textureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textureView = findViewById(R.id.texture_view);
        //MainActivity mainActivity = this;

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            //textureView.post { startCamera() }
            textureView.post(() -> startCamera());
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private void startCamera() {
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                // We want to show input from back camera of the device
                .setLensFacing(CameraX.LensFacing.BACK)
                .build();
        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(output -> {
            textureView.setSurfaceTexture(output.getSurfaceTexture());
        });

        ImageAnalysisConfig imageAnalysisConfig = new ImageAnalysisConfig.Builder().build();
        ImageAnalysis imageAnalysis = new ImageAnalysis(imageAnalysisConfig);

        QrCodeAnalyzer qrCodeAnalyzer = new QrCodeAnalyzer(qrCodes -> {
            for (FirebaseVisionBarcode barcode : qrCodes) {
//                Log.d("MainActivity", "QR Code detected: " + barcode.getRawValue() + ".");
                Toast.makeText(getApplicationContext(), barcode.getRawValue(), Toast.LENGTH_SHORT).show();
            }
        });
        imageAnalysis.setAnalyzer(qrCodeAnalyzer);
        CameraX.bindToLifecycle((LifecycleOwner) this, preview, imageAnalysis);
    }

    private boolean isCameraPermissionGranted() {
        return  ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
}

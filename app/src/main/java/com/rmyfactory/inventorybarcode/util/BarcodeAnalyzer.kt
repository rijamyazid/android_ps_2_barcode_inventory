package com.rmyfactory.inventorybarcode.util

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    val onScanSuccess: (barcodeId: String?) -> Unit
) : ImageAnalysis.Analyzer {

    private val barcodeScanner by lazy {
        val optionsBuilder = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
        BarcodeScanning.getClient(optionsBuilder.build())
    }

    private var failureOccurred = false
    private var failureTimestamp = 0L
    private var scanTimestamp = 0L

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        if (image.image == null) return

        if (failureOccurred && System.currentTimeMillis() - failureTimestamp < 1000L) {
            image.close()
            return
        }

        failureOccurred = false
        barcodeScanner.process(image.toInputImage())
            .addOnSuccessListener { codes ->
                codes.mapNotNull { it }.firstOrNull()?.let {
                    if (System.currentTimeMillis() - scanTimestamp > 2000L) {
                        onScanSuccess(it.displayValue)
                        scanTimestamp = System.currentTimeMillis()
                    }
                }
                image.close()
            }
            .addOnFailureListener {
                failureOccurred = true
                failureTimestamp = System.currentTimeMillis()
                image.close()
            }
            .addOnCompleteListener {
                image.close()
            }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun ImageProxy.toInputImage() = InputImage.fromMediaImage(this.image!!, this.imageInfo.rotationDegrees)
}
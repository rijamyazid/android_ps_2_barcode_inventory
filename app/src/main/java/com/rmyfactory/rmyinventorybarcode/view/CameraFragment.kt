package com.rmyfactory.rmyinventorybarcode.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.rmyfactory.rmyinventorybarcode.R
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentCameraBinding
import com.rmyfactory.rmyinventorybarcode.util.BarcodeAnalyzer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private lateinit var cameraExecutor: ExecutorService

    private val perReqLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        val granted = it.entries.all {
            it.value == true
        }
        if (granted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.camera_not_granted),
                Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setupCamera()
        if (cameraPermissionsGranted()) {
            startCamera()
        } else {
            perReqLauncher.launch(REQUIRED_PERMISSIONS)
        }
    }

    override fun onResume() {
        super.onResume()
        imageAnalyzer.clearAnalyzer()
        imageAnalyzer.setAnalyzer(cameraExecutor, barcodeAnalyzer)
    }

    private fun setupCamera() {
        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetResolution(Size(640, 480)) //1280,780
            .build()

        barcodeAnalyzer = BarcodeAnalyzer(
            onScanSuccess = { barcode ->
                barcode?.let {
                    onScanSuccess(barcode)
                }
            })
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

            cameraProvider = cameraProviderFuture.get()

            cameraPreview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraX.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, cameraPreview, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun cameraPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_PERMISSIONS.first()) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun onScanSuccess(itemId: String?) {
        itemId?.let {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
        }
//        navigateToItemActivity(itemId)
    }

    companion object {
        private const val TAG = "RMYFactory"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}
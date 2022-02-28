package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.util.concurrent.ListenableFuture
import com.rmyfactory.rmyinventorybarcode.R
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentTransactionBinding
import com.rmyfactory.rmyinventorybarcode.util.BarcodeAnalyzer
import com.rmyfactory.rmyinventorybarcode.view.adapter.OrderAdapter
import com.rmyfactory.rmyinventorybarcode.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var orderAdapter: OrderAdapter

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private lateinit var cameraExecutor: ExecutorService

    private var scanTimeStamp = 0L

    private val perReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val granted = it.entries.all {
                it.value == true
            }
            if (granted) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.camera_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVars()
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }

        setupCamera()
        if (cameraPermissionsGranted()) {
            startCamera()
        } else {
            perReqLauncher.launch(CameraFragment.REQUIRED_PERMISSIONS)
        }

    }

    private fun initVars() {
        orderAdapter = OrderAdapter()
    }

    override fun onResume() {
        super.onResume()
        scanTimeStamp = 0L
        orderAdapter.addOrder(viewModel.itemList)
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
                if (System.currentTimeMillis() - scanTimeStamp > 2000L) {
                    barcode?.let {
                        onScanSuccess(it)
                    }
                    scanTimeStamp = System.currentTimeMillis()
                }
            })
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

            cameraProvider = cameraProviderFuture.get()

            cameraPreview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraTransaction.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, cameraPreview, imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(CameraFragment.TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun cameraPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), CameraFragment.REQUIRED_PERMISSIONS.first()
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun onScanSuccess(itemId: String?) {
        itemId?.let {
            viewModel.readItemById(it).observe(viewLifecycleOwner, { item ->
                item?.let { _item ->
                    if (!checkIdOnMapList(_item.itemId, viewModel.itemList)) {
                        val orderMap = mapOf(
                            "orderId" to _item.itemId,
                            "orderName" to _item.itemName,
                            "orderPrice" to _item.itemPrice,
                            "orderQty" to "1"
                        )
                        viewModel.itemList.add(orderMap)
                        orderAdapter.addOrder(viewModel.itemList)
                    } else {
                        Toast
                            .makeText(
                                requireContext(),
                                "Item sudah ada dalam keranjang",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
                } ?: Toast
                    .makeText(requireContext(), "Item tidak ada dalam database", Toast.LENGTH_SHORT)
                    .show()
            })
        }
    }

    private fun checkIdOnMapList(id: String, list: List<Map<String, String>>): Boolean {

        var isInMap = false
        list.forEach { map ->
            if (map.containsKey("orderId")) {
                if (map["orderId"] == id) isInMap = true
            }
        }
        return isInMap
    }

}
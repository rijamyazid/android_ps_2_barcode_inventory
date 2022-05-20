package com.rmyfactory.inventorybarcode.view.fragment

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.util.concurrent.ListenableFuture
import com.rmyfactory.inventorybarcode.R
import com.rmyfactory.inventorybarcode.databinding.FragmentCartBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.inventorybarcode.util.BarcodeAnalyzer
import com.rmyfactory.inventorybarcode.util.Permissions
import com.rmyfactory.inventorybarcode.util.ResponseResult
import com.rmyfactory.inventorybarcode.util.toCartHolder
import com.rmyfactory.inventorybarcode.view.adapter.CartAdapter
import com.rmyfactory.inventorybarcode.viewmodel.CartViewModel
import com.rmyfactory.inventorybarcode.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CartFragment : BaseFragment() {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private lateinit var cameraExecutor: ExecutorService

    private var scanTimeStamp = 0L

    private val perReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission) {
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
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVars()
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }

        binding.btnConfirmCart.setOnClickListener {
            findNavController()
                .navigate(
                    CartFragmentDirections
                        .actionBnmTransactionsToOrderFragment(viewModel.itemList.toTypedArray())
                )
        }

        binding.fabCartAdd.setOnClickListener {
            mainActivityViewModel.productCartState = 1
            findNavController().navigate(CartFragmentDirections.actionBnvCartToBnvProduct())
        }

        binding.btnClearCart.setOnClickListener {
            viewModel.itemList.clear()
            cartAdapter.addOrder(viewModel.itemList)
        }

        mainActivityViewModel.productWithUnits.observe(viewLifecycleOwner, {
            if(mainActivityViewModel.productCartState == 2) {
                if (it != null) {
                    viewModel.itemList.add(it.toCartHolder())
                    cartAdapter.addOrder(viewModel.itemList)
                    mainActivityViewModel.productCartState = 0
                } else {
                    viewModel.itemList.clear()
                    cartAdapter.addOrder(viewModel.itemList)
                }
            }
        })

        setupCamera()
        if (cameraPermissionsGranted()) {
            startCamera()
        } else {
            perReqLauncher.launch(Permissions.CAMERA_PERMISSION)
        }

    }

    private fun initVars() {
        cartAdapter = CartAdapter { cartPos, unitPos, isIncreased ->
            viewModel.itemList[cartPos].productUnits[unitPos].productQty =
                if (isIncreased) {
                    viewModel.itemList[cartPos].productUnits[unitPos].productQty + 1
                } else {
                    viewModel.itemList[cartPos].productUnits[unitPos].productQty - 1
                }
        }
    }

    override fun onResume() {
        super.onResume()
        scanTimeStamp = 0L
        Log.d("RMYFACTORYX", "CartFrag: ${viewModel.itemList}")
        cartAdapter.addOrder(viewModel.itemList)
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
                Log.e(Permissions.TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun cameraPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Permissions.CAMERA_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun onScanSuccess(itemId: String?) {
        itemId?.let { productId ->
            viewModel.readProductWithUnitsById(productId)
            viewModel.productWithUnitsResult.observe(viewLifecycleOwner, { product ->

                when(product) {
                    is ResponseResult.Loading -> {}
                    is ResponseResult.Success -> {
                        if (!checkIdOnList(product.data.product.productId, viewModel.itemList)) {
                            viewModel.itemList.add(product.data.toCartHolder())
                            cartAdapter.addOrder(viewModel.itemList)
                        } else {
                            Toast
                                .makeText(
                                    requireContext(),
                                    "Item sudah ada dalam keranjang",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                    is ResponseResult.Failure -> {
                        Toast
                            .makeText(requireContext(), "Item tidak ada dalam database", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {}
                }
            })
        }
    }

    private fun checkIdOnList(id: String, list: List<CartHolder>): Boolean {

        var isInList = false
        list.forEach { product ->
            if (product.productId == id) {
                isInList = true
            }
        }
        return isInList
    }

}
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.util.concurrent.ListenableFuture
import com.rmyfactory.rmyinventorybarcode.R
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentCartBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.rmyinventorybarcode.util.BarcodeAnalyzer
import com.rmyfactory.rmyinventorybarcode.util.Permissions
import com.rmyfactory.rmyinventorybarcode.util.toCartHolder
import com.rmyfactory.rmyinventorybarcode.view.adapter.CartAdapter
import com.rmyfactory.rmyinventorybarcode.viewmodel.ProductCartViewModel
import com.rmyfactory.rmyinventorybarcode.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CartFragment : BaseFragment() {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: TransactionViewModel by viewModels()
    private val productCartViewModel: ProductCartViewModel by activityViewModels()
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
                        .actionBnmTransactionsToOrderConfirmationFragment(viewModel.itemList.toTypedArray())
                )
//            findNavController()
//                .navigate(
//                    TransactionFragmentDirections
//                        .actionBnmTransactionsToOrderConfirmationActivity(viewModel.itemList.toTypedArray())
//                )
        }

        binding.fabCartAdd.setOnClickListener {
            productCartViewModel.setProductCartState(1)
            findNavController().navigate(CartFragmentDirections.actionBnvCartToBnvProduct())
        }

        binding.btnClearCart.setOnClickListener {
            viewModel.itemList.clear()
            cartAdapter.addOrder(viewModel.itemList)
        }

        productCartViewModel.productCartState.observe(viewLifecycleOwner, {
            if(it == 2) {
                viewModel.itemList.add(productCartViewModel.productWithUnits.toCartHolder())
                productCartViewModel.setProductCartState(0)
                cartAdapter.addOrder(viewModel.itemList)
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
        itemId?.let {
            viewModel.readProductByIdWithUnits(it).observe(viewLifecycleOwner, { item ->
                item?.let { _item ->
                    if (!checkIdOnList(_item.product.productId, viewModel.itemList)) {
//                        val order = OrderHolder(
//                            _item.item.itemId,
//                            _item.item.itemName,
//                            _item.itemUnitList[0].itemUnit.price,
//                            1
//                        )
//                        val orderMap = mutableMapOf(
//                            "orderId" to _item.itemId,
//                            "orderName" to _item.itemName,
//                            "orderPrice" to _item.itemPrice,
//                            "orderQty" to "1"
//                        )
                        viewModel.itemList.add(_item.toCartHolder())
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
                } ?: Toast
                    .makeText(requireContext(), "Item tidak ada dalam database", Toast.LENGTH_SHORT)
                    .show()
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
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.util.concurrent.ListenableFuture
import com.rmyfactory.inventorybarcode.R
import com.rmyfactory.inventorybarcode.databinding.FragmentCartBinding
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder2
import com.rmyfactory.inventorybarcode.util.*
import com.rmyfactory.inventorybarcode.view.adapter.CartAdapter
import com.rmyfactory.inventorybarcode.view.adapter.CartSingleUnitAdapter
import com.rmyfactory.inventorybarcode.view.bottomsheet_modal.CartModalBottomSheet
import com.rmyfactory.inventorybarcode.viewmodel.CartViewModel
import com.rmyfactory.inventorybarcode.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CartFragment : BaseFragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartSingleUnitAdapter: CartSingleUnitAdapter

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraExecutor: ExecutorService? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var barcodeAnalyzer: BarcodeAnalyzer? = null
    private var cameraPreview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraSelector: CameraSelector? = null
    var modalBottomSheet: CartModalBottomSheet? = null

    private var scanTimeStamp = 0L
    private val SCAN_INTERVAL = 1000L

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
        _binding = FragmentCartBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVars()
        setupCamera()
        if (cameraPermissionsGranted()) {
            startCamera()
        } else {
            perReqLauncher.launch(Permissions.CAMERA_PERMISSION)
        }

        binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(requireContext())
//            adapter = cartAdapter
            adapter = cartSingleUnitAdapter
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

    }

    private fun initVars() {
        cartSingleUnitAdapter = CartSingleUnitAdapter()
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
        imageAnalyzer?.clearAnalyzer()
        imageAnalyzer?.setAnalyzer(cameraExecutor!!, barcodeAnalyzer!!)
    }

    private fun setupCamera() {

        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraPreview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.cameraTransaction.surfaceProvider)
        }

        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetResolution(Size(640, 480)) //1280,780
            .build()

        barcodeAnalyzer = BarcodeAnalyzer(
            onScanSuccess = { barcode ->
                Log.d("RMYINVENTORY: CART ", "timeInMillis = ${System.currentTimeMillis()}, scanTime = ${scanTimeStamp}, timeInMillis - scanTime = ${System.currentTimeMillis() - scanTimeStamp}")
                if (System.currentTimeMillis() - scanTimeStamp > 3000L) {
                    scanTimeStamp = System.currentTimeMillis()
                    barcode?.let {
                        onScanSuccess(it)
                    }
                }
            })
    }

    private fun startCamera() {
        cameraProviderFuture?.addListener({
            cameraProvider = cameraProviderFuture?.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    fun bindCameraUseCases() {
        cameraProvider?.unbindAll()
        try {
            cameraProvider?.bindToLifecycle(
                this, cameraSelector!!, cameraPreview, imageAnalyzer
            )
        } catch (exc: Exception) {
            Log.e(Permissions.TAG, "Use case binding failed", exc)
        }
    }

    fun unbindCameraUseCase() {
        cameraProvider?.unbindAll()
    }

    private fun cameraPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Permissions.CAMERA_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        cameraProvider = null
        cameraExecutor?.shutdown()
        cameraExecutor = null
        cameraProviderFuture = null
        barcodeAnalyzer = null
        cameraPreview = null
        imageAnalyzer = null
        cameraSelector = null
    }

    private fun onScanSuccess(itemId: String?) {
        itemId?.let {
            modalBottomSheet = CartModalBottomSheet(this)
            lifecycleScope.launchWhenStarted {
                val productWithUnits = withContext(Dispatchers.IO) {
                    viewModel.susReadProductWithUnitsById(it)
                }

                if(productWithUnits != null) {
                    val productId = productWithUnits.product.productId
                    if(productWithUnits.isSingleUnit()) {
                        val unitId = productWithUnits.productUnitList[0].unit.unitId
                        if(viewModel.productMap[productId]?.get(unitId) == null) {
                            viewModel.productMap[productId] = mutableMapOf(unitId to true)
                            viewModel.itemList2.add(productWithUnits.toCHDomainSingleUnit())
                            cartSingleUnitAdapter.submitToCart(viewModel.itemList2)
                        }
                    } else {
                        modalBottomSheet?.let {
                            it.submitData(productWithUnits, viewModel.productMap)
                            it.onUnitClickListener(object: CartModalBottomSheet.OnUnitClickListener {
                                override fun onUnitClick(cartHolders: List<CartHolder2>) {
                                    if(cartHolders.size == 1) {
                                        if (viewModel.productMap[cartHolders[0].productId]?.get(
                                                cartHolders[0].productUnit
                                            ) == null
                                        ) {
                                            viewModel.productMap[cartHolders[0].productId] =
                                                mutableMapOf(cartHolders[0].productUnit to true)
                                            viewModel.itemList2.add(cartHolders[0])
                                            cartSingleUnitAdapter.submitToCart(viewModel.itemList2)
                                        }
                                    }
                                }
                            })
                            it.show(childFragmentManager, CartModalBottomSheet.TAG)
                        }
                    }
                }
            }
        }
    }

//    private fun onScanSuccess(itemId: String?) {
//        itemId?.let { productId ->
//            viewModel.readProductWithUnitsById(productId)
//            viewModel.productWithUnitsResult.observe(viewLifecycleOwner, { product ->
//
//                when(product) {
//                    is ResponseResult.Loading -> {}
//                    is ResponseResult.Success -> {
////                        if (!checkIdOnList(product.data.product.productId, viewModel.itemList)) {
//                            viewModel.itemList.add(product.data.toCartHolder())
//                            cartAdapter.addOrder(viewModel.itemList)
//
//                            modalBottomSheet = CartModalBottomSheet(this)
//                            modalBottomSheet?.let {
//                                it.submitData(product.data)
////                                it.dismiss()
//                                it.show(childFragmentManager, CartModalBottomSheet.TAG)
//                            }
//
////                        } else {
////                            Toast
////                                .makeText(
////                                    requireContext(),
////                                    "Item sudah ada dalam keranjang",
////                                    Toast.LENGTH_SHORT
////                                )
////                                .show()
////                        }
//                    }
//                    is ResponseResult.Failure -> {
//                        Toast
//                            .makeText(requireContext(), "Item tidak ada dalam database", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                    else -> {}
//                }
//            })
//        }
//    }

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
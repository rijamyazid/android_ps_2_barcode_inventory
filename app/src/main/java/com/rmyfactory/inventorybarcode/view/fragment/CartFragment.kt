package com.rmyfactory.inventorybarcode.view.fragment

import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.ListenableFuture
import com.rmyfactory.inventorybarcode.R
import com.rmyfactory.inventorybarcode.databinding.FragmentCartBinding
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

    companion object {
        var IS_CART_MODAL_OPEN = false
    }

    private val cameraPermissionRequest = singlePermissionRequest { result ->
        if (result) {
            startCamera()
        } else {
            toastMessage(getString(R.string.camera_not_granted))
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
        if (hasPermission(requireContext(), Permissions.CAMERA_PERMISSION)) {
            startCamera()
        } else {
            cameraPermissionRequest.launch(Permissions.CAMERA_PERMISSION)
        }

        binding.apply {
            tvCartEmptyTitle.text = resources.getString(R.string.lbl_cart_empty_title)
            tvCartEmptyCaption.text = resources.getString(R.string.lbl_cart_empty_caption)
        }
        Glide.with(this).load(R.drawable.img_cart_empty)
            .placeholder(R.drawable.img_cart_empty)
            .into(binding.imgCartEmptyLogo)

        binding.rvTransaction.apply {
            layoutManager = LinearLayoutManager(requireContext())
//            adapter = cartAdapter
            adapter = cartSingleUnitAdapter
        }
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.absoluteAdapterPosition
                val product = viewModel.productList[pos]
                viewModel.productList.removeAt(pos)
                if ((viewModel.productMap[product.productId]?.size ?: 0) > 1) {
                    viewModel.productMap[product.productId]?.remove(product.productUnit)
                } else {
                    viewModel.productMap.remove(product.productId)
                }
                cartSingleUnitAdapter.removeFromCartPos(pos)
//                cartSingleUnitAdapter.removeFromCartPos(pos)
            }
        }).attachToRecyclerView(binding.rvTransaction)

        binding.btnConfirmCart.setOnClickListener {
            findNavController()
                .navigate(
                    CartFragmentDirections
                        .actionBnmTransactionsToOrderFragment(viewModel.productList.toTypedArray())
                )
        }

        binding.fabCartAdd.setOnClickListener {
            mainActivityViewModel.productCartState = 1
            findNavController().navigate(CartFragmentDirections.actionBnvCartToBnvProduct())
        }

        binding.btnClearCart.setOnClickListener {
            viewModel.productList.clear()
            viewModel.productMap.clear()
            cartSingleUnitAdapter.clearCart()
//            cartSingleUnitAdapter.clearCart()
            setVisibleEmptyCart(true)
        }

        mainActivityViewModel.productWithUnits.observe(viewLifecycleOwner) {
            if (mainActivityViewModel.productCartState == 2) {
                if (it != null) {
                    val productId = it.product.productId
                    if (it.isSingleUnit()) {
                        val unitId = it.productUnitList[0].unit.unitId
                        if (viewModel.productMap[productId]?.get(unitId) == null) {
                            viewModel.productMap[productId] = mutableMapOf(unitId to true)
                            viewModel.productList.add(0, it.toCHDomainSingleUnit())

                            setVisibleEmptyCart(false)
                            cartSingleUnitAdapter.submitToCart(
                                viewModel.productList,
                                CartSingleUnitAdapter.ANIME_TYPE_INSERT
                            )
                        }
                    } else {
                        modalBottomSheet = CartModalBottomSheet(this)
                        modalBottomSheet?.let { modal ->
                            modal.submitData(it, viewModel.productMap)
                            modal.onUnitClickListener(object :
                                CartModalBottomSheet.OnUnitClickListener {
                                override fun onUnitClick(cartHolders: List<CartHolder2>) {
                                    if (cartHolders.size == 1) {
                                        if (viewModel.productMap[cartHolders[0].productId] == null) {
                                            viewModel.productMap[cartHolders[0].productId] =
                                                mutableMapOf(
                                                    cartHolders[0].productUnit to true
                                                )
                                        } else {
                                            viewModel.productMap[cartHolders[0].productId]?.put(
                                                cartHolders[0].productUnit, true
                                            )
                                        }
                                        viewModel.productList.add(0, cartHolders[0])

                                        setVisibleEmptyCart(false)
                                        cartSingleUnitAdapter.submitToCart(
                                            viewModel.productList,
                                            CartSingleUnitAdapter.ANIME_TYPE_INSERT
                                        )
                                    }
                                }
                            })
                            modal.show(childFragmentManager, CartModalBottomSheet.TAG)
                        }
                    }
                    mainActivityViewModel.productCartState = 0
                } else {
                    viewModel.productList.clear()
                    viewModel.productMap.clear()

                    setVisibleEmptyCart(true)
                    cartSingleUnitAdapter.submitToCart(
                        viewModel.productList,
                        CartSingleUnitAdapter.ANIME_TYPE_INSERT
                    )
                }
            }
        }

    }

    private fun initVars() {
        cartSingleUnitAdapter = CartSingleUnitAdapter { pos, isIncrease ->
            logger(msg = "clicked position = $pos\nis_increased = $isIncrease\nproducts = ${viewModel.productList}")
            val currentQty = viewModel.productList[pos].productQty
            if (isIncrease) {
                viewModel.productList[pos].productQty = currentQty + 1
            } else {
                viewModel.productList[pos].productQty = currentQty - 1
            }
        }
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
        Log.d("RMYFACTORYX", "CartFrag: ${viewModel.productList}")

        if (viewModel.productList.isEmpty()) {
            setVisibleEmptyCart(true)
        } else {
            setVisibleEmptyCart(false)
        }
        cartSingleUnitAdapter.refreshCart(viewModel.productList)
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
                Log.d(
                    "RMYINVENTORY: CART ",
                    "timeInMillis = ${System.currentTimeMillis()}, scanTime = ${scanTimeStamp}, timeInMillis - scanTime = ${System.currentTimeMillis() - scanTimeStamp}"
                )
                if (System.currentTimeMillis() - scanTimeStamp > 3000L && !IS_CART_MODAL_OPEN) {
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

                if (productWithUnits != null) {
                    val productId = productWithUnits.product.productId
                    if (productWithUnits.isSingleUnit()) {
                        val unitId = productWithUnits.productUnitList[0].unit.unitId
                        if (viewModel.productMap[productId]?.get(unitId) == null) {
                            viewModel.productMap[productId] = mutableMapOf(unitId to true)
                            viewModel.productList.add(0, productWithUnits.toCHDomainSingleUnit())

                            setVisibleEmptyCart(false)
                            cartSingleUnitAdapter.submitToCart(
                                viewModel.productList,
                                CartSingleUnitAdapter.ANIME_TYPE_INSERT
                            )
                        }
                    } else {
                        modalBottomSheet?.let {
                            it.submitData(productWithUnits, viewModel.productMap)
                            it.onUnitClickListener(object :
                                CartModalBottomSheet.OnUnitClickListener {
                                override fun onUnitClick(cartHolders: List<CartHolder2>) {
                                    if (cartHolders.size == 1) {
                                        if (viewModel.productMap[cartHolders[0].productId] == null) {
                                            Log.d(
                                                "CartFragmentt",
                                                "before = " + viewModel.productMap.toString()
                                            )
                                            viewModel.productMap[cartHolders[0].productId] =
                                                mutableMapOf(
                                                    cartHolders[0].productUnit to true
                                                )
                                            Log.d(
                                                "CartFragmentt",
                                                "after = " + viewModel.productMap.toString()
                                            )

                                        } else {
                                            Log.d(
                                                "CartFragmentt",
                                                "before = " + viewModel.productMap.toString()
                                            )
                                            viewModel.productMap[cartHolders[0].productId]?.put(
                                                cartHolders[0].productUnit, true
                                            )
                                            Log.d(
                                                "CartFragmentt",
                                                "after = " + viewModel.productMap.toString()
                                            )
                                        }
                                        viewModel.productList.add(0, cartHolders[0])

                                        setVisibleEmptyCart(false)
                                        cartSingleUnitAdapter.submitToCart(
                                            viewModel.productList,
                                            CartSingleUnitAdapter.ANIME_TYPE_INSERT
                                        )
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

    private fun setVisibleEmptyCart(state: Boolean) {
        if (state) {
            if (binding.llCartEmpty.visibility == View.GONE) {
                binding.rvTransaction.visibility = View.GONE
                binding.llCartEmpty.visibility = View.VISIBLE
            }
        } else {
            if (binding.rvTransaction.visibility == View.GONE) {
                binding.rvTransaction.visibility = View.VISIBLE
                binding.llCartEmpty.visibility = View.GONE
            }
        }
    }

}
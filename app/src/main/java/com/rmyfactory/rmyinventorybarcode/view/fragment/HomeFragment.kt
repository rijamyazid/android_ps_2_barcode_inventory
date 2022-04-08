package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.ListenableFuture
import com.rmyfactory.rmyinventorybarcode.R
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentHomeBinding
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.BaseModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.ItemUnitModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.util.BarcodeAnalyzer
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_ITEM
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_ITEM_UNIT
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_ORDER
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_ORDER_ITEM
import com.rmyfactory.rmyinventorybarcode.util.Functions.CONSTANT_TABLE_UNIT
import com.rmyfactory.rmyinventorybarcode.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var cameraSelector: CameraSelector


    private var isScanSuccess = false

    private val perReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { permission ->
                permission.value == true
            }
            if (!granted) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.camera_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }

    private val perWriteExportLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val granted = permissions.entries.all { permission ->
                permission.value == true
            }

            if (granted) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_TITLE, "${System.currentTimeMillis()}_dataset.txt")
                resultWriteExport.launch(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.camera_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }

        }

    private val perWriteImportLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val granted = permissions.entries.all { permission ->
                permission.value == true
            }

            if (granted) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "text/plain"
                resultWriteImport.launch(intent)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.camera_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }

        }

    private val resultWriteExport =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val userChosenUri = it.data?.data
                val outStream = requireContext().contentResolver.openOutputStream(userChosenUri!!)
                CoroutineScope(Dispatchers.IO).launch {

                    val listOfItemModel = withContext(Dispatchers.Main) {
                        viewModel._readItems()
                    }
                    var exportContent = "#item_table\n"
                    listOfItemModel.forEach { item ->
                        exportContent += "${item.itemId};${item.itemName};${item.itemNote}\n"
                    }

                    val listOfOrderModel = withContext(Dispatchers.Main) {
                        viewModel._readOrders()
                    }
                    exportContent += "\n#order_table\n"
                    listOfOrderModel.forEach { order ->
                        exportContent += "${order.orderId};${order.orderPay};${order.orderExchange};${order.orderTotalPrice}\n"
                    }

                    val listOfUnitModel = withContext(Dispatchers.Main) {
                        viewModel._readUnits()
                    }
                    exportContent += "\n#unit_table\n"
                    listOfUnitModel.forEach { unit ->
                        exportContent += "${unit.unitId}\n"
                    }

                    val listOfOrderItemModel = withContext(Dispatchers.Main) {
                        viewModel._readOrderItems()
                    }
                    exportContent += "\n#order_item_table\n"
                    listOfOrderItemModel.forEach { orderItem ->
                        exportContent += "${orderItem.orderId};${orderItem.itemId};${orderItem.qty};${orderItem.price};${orderItem.totalPrice}\n"
                    }

                    val listOfItemUnitModel = withContext(Dispatchers.Main) {
                        viewModel._readItemUnits()
                    }
                    exportContent += "\n#item_unit_table\n"
                    listOfItemUnitModel.forEach { itemUnit ->
                        exportContent += "${itemUnit.id};${itemUnit.itemId};${itemUnit.unitId};${itemUnit.stock};${itemUnit.price}\n"
                    }

                    exportContent.byteInputStream().use { input ->
                        outStream.use { output ->
                            input.copyTo(output!!)
                        }
                    }
                }
            }
        }

    private val resultWriteImport =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val userChosenUri = it.data?.data
                val inStream = requireContext().contentResolver.openInputStream(userChosenUri!!)

                val mapOfTables = mutableMapOf<String, MutableList<BaseModel>>()
                var currentTable = "none"

                CoroutineScope(Dispatchers.IO).launch {
                    inStream.use { input ->
                        input?.reader()?.forEachLine { line ->
//                            Log.d("RMYFACTORYX", line)
                            if(line.isNotEmpty()) {
                                if (line.first() == '#') {
                                    currentTable = line.substring(1)
                                    mapOfTables[currentTable] = mutableListOf()
                                } else {
                                    val lineSplit = line.split(";")
                                    if (lineSplit.isNotEmpty()) {
                                        when (currentTable) {
                                            CONSTANT_TABLE_ITEM -> {
                                                mapOfTables[currentTable]?.add(
                                                    ItemModel(
                                                        itemId = lineSplit[0],
                                                        itemName = lineSplit[1],
                                                        itemNote = lineSplit[2]
                                                    )
                                                )
                                            }
                                            CONSTANT_TABLE_ORDER -> {
                                                mapOfTables[currentTable]?.add(
                                                    OrderModel(
                                                        orderId = lineSplit[0],
                                                        orderPay = lineSplit[1],
                                                        orderExchange = lineSplit[2],
                                                        orderTotalPrice = lineSplit[3]
                                                    )
                                                )
                                            }
                                            CONSTANT_TABLE_UNIT -> {
                                                mapOfTables[currentTable]?.add(
                                                    UnitModel(
                                                        unitId = lineSplit[0]
                                                    )
                                                )
                                            }
                                            CONSTANT_TABLE_ORDER_ITEM -> {
                                                mapOfTables[currentTable]?.add(
                                                    OrderItemModel(
                                                        orderId = lineSplit[0],
                                                        itemId = lineSplit[1],
                                                        qty = lineSplit[2].toInt(),
                                                        price = lineSplit[3],
                                                        totalPrice = lineSplit[4]
                                                    )
                                                )
                                            }
                                            CONSTANT_TABLE_ITEM_UNIT -> {
                                                mapOfTables[currentTable]?.add(
                                                    ItemUnitModel(
                                                        id = lineSplit[0].toLong(),
                                                        itemId = lineSplit[1],
                                                        unitId = lineSplit[2],
                                                        stock = lineSplit[3].toInt(),
                                                        price = lineSplit[4]
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        viewModel.importData(mapOfTables)
                    }
                    Log.d("RMYFACTORYX", mapOfTables.toString())
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("RMYFACTORYX", "onViewCreated")

        setupCamera()
        startCamera()

        binding.btnLog.apply {
            setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionBnvHomeToOrderLogFragment()
                )
            }
        }

        binding.btnHomeExport.setOnClickListener {
            if (readWritePermissionsGranted()) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_TITLE, "${System.currentTimeMillis()}_dataset.txt")
                resultWriteExport.launch(intent)
            } else {
                perWriteExportLauncher.launch(READ_WRITE_PERMISSION)
            }
        }

        Glide.with(this).load(R.drawable.home_ic_history).placeholder(R.drawable.home_ic_history).into(binding.btnLog)
        Glide.with(this).load(R.drawable.home_ic_import).placeholder(R.drawable.home_ic_import).into(binding.btnHomeImport)
        Glide.with(this).load(R.drawable.home_ic_export).placeholder(R.drawable.home_ic_export).into(binding.btnHomeExport)
        Glide.with(this).load(R.drawable.camera_slider_left).placeholder(R.drawable.camera_slider_left).into(binding.imgLeftSlider)
        Glide.with(this).load(R.drawable.camera_slider_right).placeholder(R.drawable.camera_slider_right).into(binding.imgRightSlider)

        binding.btnHomeImport.setOnClickListener {
            if (readWritePermissionsGranted()) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "text/plain"
                resultWriteImport.launch(intent)
            } else {
                perWriteImportLauncher.launch(READ_WRITE_PERMISSION)
            }
        }

        binding.btnStartScan.setOnTouchListener { v, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
//                    cameraSliderToggle(false)
//                    binding.imgLeftSlider.animate().setDuration(500).translationX(-100.0f)
//                    binding.imgRightSlider.animate().setDuration(500).translationX(100.0f)
                    binding.imgLeftSlider.slideRight(0f, binding.imgLeftSlider.width.toFloat() * -1)
                    binding.imgRightSlider.slideRight()
                    bindCameraUseCases()
                }
                MotionEvent.ACTION_UP -> {
//                    cameraSliderToggle(true)
//                    binding.imgLeftSlider.animate().setDuration(500).translationX(0f)
//                    binding.imgRightSlider.animate().setDuration(500).translationX(0f)
                    binding.imgLeftSlider.slideLeft(binding.imgLeftSlider.width.toFloat() * -1, 0f)
                    binding.imgRightSlider.slideLeft()
                    unbindCameraUseCase()
                    v.performClick()
                }
            }
            true
        }
    }

    private fun View.slideRight(from: Float = 0f, to:Float = 0f, time: Long = 500) {
        val width = this.width.toFloat()
        var fromValue = 0f
        var toValue = width
        if(from != 0f || to != 0f) {
            fromValue = from
            toValue = to
        }
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, fromValue, toValue).apply {
            duration = time
            start()
        }
    }

    private fun View.slideLeft(from: Float = 0f, to:Float = 0f, time: Long = 500) {
        val width = this.width.toFloat()
        var fromValue = width
        var toValue = 0f
        if(from != 0f || to != 0f) {
            fromValue = from
            toValue = to
        }
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, fromValue, toValue).apply {
            duration = time
            start()
        }
    }

    private fun cameraSliderToggle(show: Boolean) {
        val transitionStart = Slide(Gravity.START)
        transitionStart.duration = 500
        transitionStart.addTarget(binding.imgLeftSlider)
        TransitionManager.beginDelayedTransition(binding.contCamera, transitionStart)
        binding.imgLeftSlider.visibility = if(show) View.VISIBLE else View.GONE

        val transitionEnd = Slide(Gravity.START)
        transitionEnd.duration = 500
        transitionEnd.addTarget(binding.imgRightSlider)
        TransitionManager.beginDelayedTransition(binding.contCamera, transitionEnd)
        binding.imgRightSlider.visibility = if(show) View.VISIBLE else View.GONE

    }

    override fun onResume() {
        super.onResume()
        isScanSuccess = false

        Log.d("RMYFACTORYX", "onResume")
        binding.imgLeftSlider.slideLeft(binding.imgLeftSlider.width.toFloat() * -1, 0f, time = 0)
        binding.imgRightSlider.slideLeft(time = 0)

        if (!cameraPermissionsGranted()) {
            perReqLauncher.launch(REQUIRED_PERMISSIONS)
        }

        imageAnalyzer.clearAnalyzer()
        imageAnalyzer.setAnalyzer(cameraExecutor, barcodeAnalyzer)
    }

    override fun onPause() {
        super.onPause()
        unbindCameraUseCase()
    }

    private fun setupCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraPreview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.cameraX.surfaceProvider)
        }

        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetResolution(Size(640, 480)) //1280,780
            .build()

        barcodeAnalyzer = BarcodeAnalyzer(
            onScanSuccess = { barcode ->
                if (!isScanSuccess) {
                    barcode?.let {
                        onScanSuccess(it)
                    }.also {
                        isScanSuccess = true
                    }
                }
            })

    }

    private fun startCamera() {
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
//            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        cameraProvider.unbindAll()
        try {
            cameraProvider.bindToLifecycle(
                this, cameraSelector, cameraPreview, imageAnalyzer
            )
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun unbindCameraUseCase() {
        cameraProvider.unbindAll()
    }

    private fun cameraPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_PERMISSIONS.first()
    ) == PackageManager.PERMISSION_GRANTED

    private fun readWritePermissionsGranted(): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            READ_WRITE_PERMISSION.first()
        ) == PackageManager.PERMISSION_GRANTED
        val readPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            READ_WRITE_PERMISSION.last()
        ) == PackageManager.PERMISSION_GRANTED
        return writePermission && readPermission
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun onScanSuccess(itemId: String?) {
        itemId?.let {
            navigateToDetailActivity(it)
        }
    }

    private fun navigateToDetailActivity(itemId: String) {
        findNavController().navigate(HomeFragmentDirections.actionBnmScanToDetailActivity(itemId))
    }

    companion object {
        const val TAG = "RMYFactory"
        private const val REQUEST_CODE_PERMISSIONS = 10
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        val READ_WRITE_PERMISSION = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

}
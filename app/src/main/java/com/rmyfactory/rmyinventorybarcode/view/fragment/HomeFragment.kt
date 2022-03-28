package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.rmyfactory.rmyinventorybarcode.R
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentHomeBinding
import com.rmyfactory.rmyinventorybarcode.util.BarcodeAnalyzer
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

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalyzer: ImageAnalysis
    private lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private lateinit var cameraExecutor: ExecutorService

    private var isScanSuccess = false

    private val perReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all { permission ->
                permission.value == true
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

    private val perWriteExportLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val granted = permissions.entries.all { permission ->
                permission.value == true
            }

            if (granted) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.type = "*/*"
                resultWriteExportUnused.launch(intent)
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
                intent.type = "application/octet-stream"
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

    private val resultWriteExportUnused =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            val userChosenUri = it.data?.data
//            val inStream = FileInputStream("Aku Suka Kamu\nHaha Bercanda")
            val inStream = "Aku Suka Kamu\nHaha Bercanda"
            val outStream = requireContext().contentResolver.openOutputStream(userChosenUri!!)

            lifecycleScope.launchWhenCreated {
//                    val listOfItemWithUnit = withContext(Dispatchers.Main) {
//                        viewModel.readItemWithUnits_().await()
//                    }
//                    var exportContent = "#item_table\n"
//                    listOfItemWithUnit.forEach { itemModel ->
//                        exportContent += "${itemModel.item.itemId};${itemModel.item.itemName};${itemModel.item.itemNote}\n"
//                    }
//                    exportContent.byteInputStream().use { input ->
//                    input.reader().forEachLine {
//                        Log.d("RMYFACTORYX", it)
//                    }
//                Log.d("RMYFACTORYX", input.reader().readLines().toString())
//                        outStream.use { output ->
//                            input.copyTo(output!!)
//                        }
//                    }
            }
//            inStream.use { input ->
//                Log.d("RMYFACTORY", input.reader().readLines().toString())
//                outStream.use { output ->
//                    input.copyTo(output!!)
//                }
//            }
        }
    }

    private val resultWriteExport = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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

    private val resultWriteImport = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val userChosenUri = it.data?.data
            val inStream = requireContext().contentResolver.openInputStream(userChosenUri!!)
            val outStream = requireContext().getDatabasePath("db_main").outputStream()

            inStream.use { input ->
                outStream.use { output ->
                    input?.copyTo(output)
                }
            }
        }
        requireActivity().recreate()
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
        cameraExecutor = Executors.newSingleThreadExecutor()

        setupCamera()
        if (cameraPermissionsGranted()) {
            startCamera()
        } else {
            perReqLauncher.launch(REQUIRED_PERMISSIONS)
        }

        binding.btnLog.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionBnvHomeToOrderLogFragment()
            )
        }
        binding.btnHomeExport.setOnClickListener {
            if (readWritePermissionsGranted()) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.type ="*/*"
                intent.putExtra(Intent.EXTRA_TITLE, "${System.currentTimeMillis()}_dataset.txt")
                resultWriteExport.launch(intent)
            } else {
                perWriteExportLauncher.launch(READ_WRITE_PERMISSION)
            }
        }
        binding.btnHomeImport.setOnClickListener {
            if (readWritePermissionsGranted()) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/octet-stream"
                resultWriteImport.launch(intent)
            } else {
                perWriteImportLauncher.launch(READ_WRITE_PERMISSION)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isScanSuccess = false
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
        requireContext(), REQUIRED_PERMISSIONS.first()
    ) == PackageManager.PERMISSION_GRANTED

    private fun readWritePermissionsGranted(): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(requireContext(), READ_WRITE_PERMISSION.first()) == PackageManager.PERMISSION_GRANTED
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), READ_WRITE_PERMISSION.last()) == PackageManager.PERMISSION_GRANTED
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
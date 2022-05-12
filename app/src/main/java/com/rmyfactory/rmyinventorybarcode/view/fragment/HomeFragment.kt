package com.rmyfactory.rmyinventorybarcode.view.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.common.util.concurrent.ListenableFuture
import com.rmyfactory.rmyinventorybarcode.R
import com.rmyfactory.rmyinventorybarcode.databinding.FragmentHomeBinding
import com.rmyfactory.rmyinventorybarcode.util.*
import com.rmyfactory.rmyinventorybarcode.util.Permissions.TAG
import com.rmyfactory.rmyinventorybarcode.view.dialog.LoadingDialogFragment
import com.rmyfactory.rmyinventorybarcode.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraExecutor: ExecutorService? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private var barcodeAnalyzer: BarcodeAnalyzer? = null
    private var cameraPreview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraSelector: CameraSelector? = null
    private var loadingDialogFragment: LoadingDialogFragment? = null

    private var isScanSuccess = false
    private val cameraPermissionRequest = singlePermissionRequest { result ->
        if (!result) {
            toastMessage(getString(R.string.camera_not_granted))
            requireActivity().finish()
        }
    }
    private val writeExternalPermissionRequest = singlePermissionRequest { result ->
        if (result) {
            exportInitiateBehavior()
        } else {
            toastMessage(getString(R.string.camera_not_granted))
        }
    }
    private val readExternalPermissionRequest = singlePermissionRequest { result ->
        if (result) {
            importInitiateBehavior()
        } else {
            toastMessage(getString(R.string.camera_not_granted))
        }
    }

    private val activityResultExport =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val userChosenUri = it.data?.data
                val outputStream =
                    requireContext().contentResolver.openOutputStream(userChosenUri!!)

                loadingDialogFragment?.show(
                    childFragmentManager,
                    LoadingDialogFragment.TAG
                )

                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.exportDataset(
                        loadingProgress = { progress ->
                            repeat(progress) {
                                Thread.sleep(Constants.LOADING_INTERVAL_SHORT)
                                loadingDialogFragment
                                    ?.setLoadingProgress(
                                        loadingDialogFragment!!.loadingProgress
                                            .plus(Constants.LOADING_PROGRESS)
                                    )
                            }
                        },
                        loadingResult = { result ->
                            when (result) {
                                is ResultResponse.Success -> {
                                    result.data.byteInputStream().use { input ->
                                        outputStream.use { output ->
                                            input.copyTo(output!!)
                                        }
                                    }
                                    loadingDialogFragment?.dismiss()
                                }
                                is ResultResponse.Failure -> {
                                    loadingDialogFragment?.dismiss()
                                }
                                else -> {}
                            }
                        }
                    )
                }

            }
        }

    private val activityResultImport =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val userChosenUri = it.data?.data
                val inputStream =
                    requireContext().contentResolver.openInputStream(userChosenUri!!)

                loadingDialogFragment?.show(
                    childFragmentManager,
                    LoadingDialogFragment.TAG
                )

                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.importDataset(inputStream,
                        loadingProgress = { progress ->
                            repeat(progress) {
                                Thread.sleep(Constants.LOADING_INTERVAL_SHORT)
                                loadingDialogFragment
                                    ?.setLoadingProgress(
                                        loadingDialogFragment!!.loadingProgress
                                            .plus(Constants.LOADING_PROGRESS)
                                    )
                            }
                        },
                        loadingResult = { result ->
                            when(result) {
                                is ResultResponse.Success -> {
                                    loadingDialogFragment?.dismiss()
                                }
                                is ResultResponse.Failure -> {
                                    loadingDialogFragment?.dismiss()
                                }
                                else -> {}
                            }
                        }
                    )
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("RMYFACTORYX", "onViewCreated")

        setupCamera()
        startCamera()

        loadingDialogFragment = LoadingDialogFragment()
        loadingDialogFragment?.isCancelable = false

        binding.btnLog.apply {
            setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionBnvHomeToLogFragment()
                )
            }
        }

        binding.btnHomeExport.setOnClickListener {
            if (hasPermission(requireContext(), Permissions.WRITE_EXTERNAL_PERMISSION)) {
                exportInitiateBehavior()
            } else {
                writeExternalPermissionRequest.launch(Permissions.WRITE_EXTERNAL_PERMISSION)
            }
        }

        Glide.with(this).load(R.drawable.home_ic_history).placeholder(R.drawable.home_ic_history)
            .into(binding.btnLog)
        Glide.with(this).load(R.drawable.home_ic_import).placeholder(R.drawable.home_ic_import)
            .into(binding.btnHomeImport)
        Glide.with(this).load(R.drawable.home_ic_export).placeholder(R.drawable.home_ic_export)
            .into(binding.btnHomeExport)
        Glide.with(this).load(R.drawable.camera_slider_left_bright)
            .placeholder(R.drawable.camera_slider_left_bright).into(binding.imgLeftSlider)
        Glide.with(this).load(R.drawable.camera_slider_right_bright)
            .placeholder(R.drawable.camera_slider_right_bright).into(binding.imgRightSlider)

        binding.btnHomeImport.setOnClickListener {
            if (hasPermission(requireContext(), Permissions.READ_EXTERNAL_PERMISSION)) {
                importInitiateBehavior()
            } else {
                readExternalPermissionRequest.launch(Permissions.READ_EXTERNAL_PERMISSION)
            }
        }

        binding.btnStartScan.setOnTouchListener { v, motionEvent ->
            v.performClick()
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.imgLeftSlider.slideRight(0f, binding.imgLeftSlider.width.toFloat() * -1)
                    binding.imgRightSlider.slideRight()
                    bindCameraUseCases()
                }
                MotionEvent.ACTION_UP -> {
                    binding.imgLeftSlider.slideLeft(binding.imgLeftSlider.width.toFloat() * -1, 0f)
                    binding.imgRightSlider.slideLeft()
                    unbindCameraUseCase()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        isScanSuccess = false

        Log.d("RMYFACTORYX", "onResume")
        binding.imgLeftSlider.slideLeft(binding.imgLeftSlider.width.toFloat() * -1, 0f, time = 0)
        binding.imgRightSlider.slideLeft(time = 0)

        if (!hasPermission(requireContext(), Permissions.CAMERA_PERMISSION)) {
            cameraPermissionRequest.launch(Permissions.CAMERA_PERMISSION)
        }

        imageAnalyzer?.clearAnalyzer()
        imageAnalyzer?.setAnalyzer(cameraExecutor!!, barcodeAnalyzer!!)
    }

    override fun onPause() {
        super.onPause()
        unbindCameraUseCase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraProvider = null
        cameraExecutor?.shutdown()
        cameraExecutor = null
        cameraProviderFuture = null
        barcodeAnalyzer = null
        cameraPreview = null
        imageAnalyzer = null
        cameraSelector = null
        loadingDialogFragment = null
    }

    private fun View.slideRight(from: Float = 0f, to: Float = 0f, time: Long = 500) {
        val width = this.width.toFloat()
        var fromValue = 0f
        var toValue = width
        if (from != 0f || to != 0f) {
            fromValue = from
            toValue = to
        }
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, fromValue, toValue).apply {
            duration = time
            start()
        }
    }

    private fun View.slideLeft(from: Float = 0f, to: Float = 0f, time: Long = 500) {
        val width = this.width.toFloat()
        var fromValue = width
        var toValue = 0f
        if (from != 0f || to != 0f) {
            fromValue = from
            toValue = to
        }
        ObjectAnimator.ofFloat(this, View.TRANSLATION_X, fromValue, toValue).apply {
            duration = time
            start()
        }
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
        cameraProviderFuture?.addListener({
            cameraProvider = cameraProviderFuture?.get()
//            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        cameraProvider?.unbindAll()
        try {
            cameraProvider?.bindToLifecycle(
                this, cameraSelector!!, cameraPreview, imageAnalyzer
            )
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun unbindCameraUseCase() {
        cameraProvider?.unbindAll()
    }

    private fun onScanSuccess(itemId: String?) {
        itemId?.let {
            navigateToDetailActivity(it)
        }
    }

    private fun exportInitiateBehavior() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_TITLE, "inventory_dataset_${System.currentTimeMillis()}.txt")
        activityResultExport.launch(intent)
    }

    private fun importInitiateBehavior() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/plain"
        activityResultImport.launch(intent)
    }

    private fun navigateToDetailActivity(itemId: String) {
        findNavController().navigate(HomeFragmentDirections.actionBnmScanToDetailActivity(itemId))
    }
}
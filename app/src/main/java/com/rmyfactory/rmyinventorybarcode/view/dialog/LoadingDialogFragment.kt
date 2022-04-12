package com.rmyfactory.rmyinventorybarcode.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.rmyfactory.rmyinventorybarcode.databinding.DialogLoadingBinding

class LoadingDialogFragment: DialogFragment() {

    private lateinit var binding: DialogLoadingBinding
    private var _loadingProgress: Int = 0
    val loadingProgress get() = _loadingProgress

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    fun setLoadingProgress(progress: Int) {
        _loadingProgress = progress
        binding.tvDialogLoading.text = "$progress% Dalam Progress"
        if (_loadingProgress == 100) {
            _loadingProgress = 0
            dismiss()
        }
    }

    companion object {
        const val TAG = "LOADING_DIALOG_FRAGMENT"
    }

}
package com.rmyfactory.rmyinventorybarcode.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun hasPermission(context: Context, permissionName: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context, permissionName
    ) == PackageManager.PERMISSION_GRANTED
}

fun Fragment.singlePermissionRequest(requestResult: (Boolean) -> Unit): ActivityResultLauncher<String> {
    return this.registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
        requestResult(permission)
    }
}

object Permissions {
    const val TAG = "RMYFactory"
    private const val REQUEST_CODE_PERMISSIONS = 10
    const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    const val READ_EXTERNAL_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    const val WRITE_EXTERNAL_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
}
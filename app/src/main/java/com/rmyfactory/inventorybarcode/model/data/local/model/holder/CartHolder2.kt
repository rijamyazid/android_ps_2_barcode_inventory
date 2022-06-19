package com.rmyfactory.inventorybarcode.model.data.local.model.holder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartHolder2(
    var productId: String,
    var productName: String,
    var productUnit: String,
    var productQty: Int,
    var productPrice: String,
    var productStock: Int,
    var productIncrement: Float = 1.0F
) : Parcelable

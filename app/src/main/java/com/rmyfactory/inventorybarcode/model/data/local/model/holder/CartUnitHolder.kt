package com.rmyfactory.inventorybarcode.model.data.local.model.holder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartUnitHolder(

    var productPrice: String,
    var productStock: Int,
    var productQty: Int,
    var productUnit: String,
    var productIncrement: Float = 1.0F

): Parcelable

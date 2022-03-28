package com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductDetailHolder(
    var productId: String,
    var productName: String,
    var productNote: String = "",
    var productUnit: MutableList<String>,
    var productPrice: MutableList<String>,
    var productStock: MutableList<Int>,
    var productIncrement: MutableList<Float>,
    var productQty: Int
): Parcelable

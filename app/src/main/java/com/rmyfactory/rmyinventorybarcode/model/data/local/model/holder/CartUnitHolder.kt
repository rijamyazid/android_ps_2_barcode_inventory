package com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartUnitHolder(

    var productPrice: String,
    var productStock: Int,
    var productQty: Int,
    var productUnit: String

): Parcelable

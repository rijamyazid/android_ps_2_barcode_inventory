package com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartHolder(
    var productId: String,
    var productName: String,
    var productUnits: MutableList<CartUnitHolder>
): Parcelable

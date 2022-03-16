package com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderHolder(

    val itemId: String,
    val itemName: String,
    val detailOrder: ProductDetailHolder,
    val itemPrice: String,
    var itemQty: Int

) : Parcelable

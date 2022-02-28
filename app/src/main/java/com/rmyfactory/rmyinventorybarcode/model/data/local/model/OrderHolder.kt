package com.rmyfactory.rmyinventorybarcode.model.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderHolder(

    val itemId: String,
    val itemName: String,
    val itemPrice: String,
    var itemQty: Int

) : Parcelable

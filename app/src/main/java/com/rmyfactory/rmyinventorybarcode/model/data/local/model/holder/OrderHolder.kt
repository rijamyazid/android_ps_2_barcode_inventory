package com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder

data class OrderHolder(

    val productId: String,
    val productName: String,
    val productUnit: String,
    var productPrice: String,
    val productQty: Int,

)

package com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder

data class ProductDetailHolder(
    var productId: String,
    var productName: String,
    var productNote: String = "",
    var productUnit: MutableList<String>,
    var productPrice: MutableList<String>,
    var productStock: MutableList<Int>,
    var productQty: Int
)

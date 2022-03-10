package com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder

data class ProductDetailHolder(
    var productId: String,
    var productName: String,
    var productUnit: List<String>,
    var productPrice: List<String>,
    var productStock: List<Int>,
    var productQty: Int
)

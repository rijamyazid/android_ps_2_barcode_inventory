package com.rmyfactory.inventorybarcode.util

import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder2
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits

fun ProductWithUnits.isSingleUnit() : Boolean {
    return this.productUnitList.size <= 1
}

fun ProductWithUnits.toCHDomainSingleUnit() : CartHolder2 {
    return CartHolder2(
        productId = this.product.productId,
        productName = this.product.productName,
        productUnit = this.productUnitList[0].productUnit.unitId,
        productStock = this.productUnitList[0].productUnit.stock,
        productPrice = this.productUnitList[0].productUnit.price,
        productQty = 1,
        productIncrement = 1.0f
    )
}

fun ProductWithUnits.toCartMap() : MutableMap<String, MutableMap<String, MutableMap<String, String>>> {
    val productWithUnitsMap = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()
    productWithUnitsMap[this.product.productId] = mutableMapOf(
        "productName" to mutableMapOf(
            "name" to this.product.productName
        )
    )
    this.productUnitList.forEach {
        productWithUnitsMap[this.product.productId] = mutableMapOf(
            "productUnits" to mutableMapOf(
                "unitId" to it.productUnit.unitId,
                "unitPrice" to it.productUnit.price,
                "unitQty" to "0"
            )
        )
    }
    return productWithUnitsMap
}
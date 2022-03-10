package com.rmyfactory.rmyinventorybarcode.util

import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.ProductDetailHolder
import java.text.SimpleDateFormat
import java.util.*


fun String.ifEmptySetDefault(default: String): String {
    return if (this.isEmpty()) {
        default
    } else {
        this

    }
}

object Functions {

    fun millisToOrderId(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd:MM:yy:HH:mm:ss", Locale.ENGLISH)
        val dateString = sdf.format(timeInMillis)

        val dateSplit = dateString.split(":")

        return "${dateSplit[2]}${dateSplit[1]}${dateSplit[0]}${dateSplit[3]}${dateSplit[4]}${dateSplit[5]}"
    }

    fun dotPriceIND(nominal: String): String {
        if (nominal.length <= 3) return "Rp. $nominal"

        val reverseNominal = nominal.reversed()
        var dotPriced = ""
        reverseNominal.forEachIndexed { index, c ->
            dotPriced += c
            if ((index + 1) % 3 == 0) dotPriced += "."
        }
        return "Rp. ${dotPriced.reversed()}"
    }

    fun fillProductDetailHolder() = ProductDetailHolder(
        productId = "null",
        productName = "null",
        productUnit = mutableListOf(),
        productPrice = mutableListOf(),
        productStock = mutableListOf(),
        productQty = 0
    )

}
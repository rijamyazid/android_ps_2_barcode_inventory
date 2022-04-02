package com.rmyfactory.rmyinventorybarcode.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.CartUnitHolder
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.holder.ProductDetailHolder
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits
import java.text.SimpleDateFormat
import java.util.*


fun String.ifEmptySetDefault(default: String): String {
    return if (this.isEmpty()) {
        default
    } else {
        this

    }
}

fun String.dotPriceIND(): String {
    if (this.length <= 3) return "Rp. $this"

    val reverseNominal = this.reversed()
    var dotPriced = ""
    reverseNominal.forEachIndexed { index, c ->
        dotPriced += c
        if ((index + 1) % 3 == 0) dotPriced += "."
    }
    return "Rp. ${dotPriced.reversed()}"
}

fun ItemWithUnits.toCartHolder(): CartHolder {

    val cartUnitsHolder = mutableListOf<CartUnitHolder>()

    this.itemUnitList.forEach {
        cartUnitsHolder.add(
            CartUnitHolder(
                productPrice = it.itemUnit.price,
                productStock = it.itemUnit.stock,
                productUnit = it.itemUnit.unitId,
                productIncrement = it.itemUnit.increment,
                productQty = 0
            )
        )
    }

    return CartHolder(
        productId = item.itemId,
        productName = item.itemName,
        productUnits = cartUnitsHolder
    )

}

@ColorInt
fun Context.themeColor(@AttrRes attrRes: Int): Int = TypedValue()
    .apply { theme.resolveAttribute (attrRes, this, true) }
    .data

object Functions {

    const val CONSTANT_TABLE_ITEM = "item_table"
    const val CONSTANT_TABLE_ORDER = "order_table"
    const val CONSTANT_TABLE_UNIT = "unit_table"
    const val CONSTANT_TABLE_ITEM_UNIT = "item_unit_table"
    const val CONSTANT_TABLE_ORDER_ITEM = "order_item_table"

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
        productQty = 0,
        productIncrement = mutableListOf()
    )

}
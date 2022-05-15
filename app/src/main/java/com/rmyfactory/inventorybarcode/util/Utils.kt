package com.rmyfactory.inventorybarcode.util

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartUnitHolder
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.ProductDetailHolder
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import java.text.SimpleDateFormat
import java.util.*


fun String.ifEmptySetDefault(default: String): String {
    return if (this.isEmpty()) {
        default
    } else {
        this

    }
}

fun Fragment.toastMessage(message: String="", duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.requireContext(), message, duration).show()
}

fun String.toCurrencyFormat(currencyType: String = "Rp."): String {
    if (this.length <= 3) return "Rp. $this"

    val reverseNominal = this.reversed()
    var dotPriced = ""
    reverseNominal.forEachIndexed { index, c ->
        dotPriced += c
        if ((index + 1) % 3 == 0 && index != reverseNominal.length - 1) dotPriced += "."
    }
    return "$currencyType ${dotPriced.reversed()}"
}

fun <T> ResponseResult<T>.responses(
    isLoading: (Boolean?) -> Unit = {},
    isSuccess: (T) -> Unit = {},
    isFailure: (Throwable?) -> Unit = {}) {

    when(this) {
        is ResponseResult.Loading -> { isLoading(this.status) }
        is ResponseResult.Success -> { isSuccess(this.data) }
        is ResponseResult.Failure -> { isFailure(this.exception) }
        else -> {}
    }

}

fun ProductWithUnits.toCartHolder(): CartHolder {

    val cartUnitsHolder = mutableListOf<CartUnitHolder>()

    this.productUnitList.forEach {
        cartUnitsHolder.add(
            CartUnitHolder(
                productPrice = it.productUnit.price,
                productStock = it.productUnit.stock,
                productUnit = it.productUnit.unitId,
                productIncrement = it.productUnit.increment,
                productQty = 0
            )
        )
    }

    return CartHolder(
        productId = product.productId,
        productName = product.productName,
        productUnits = cartUnitsHolder
    )

}

@ColorInt
fun Context.themeColor(@AttrRes attrRes: Int): Int = TypedValue()
    .apply { theme.resolveAttribute (attrRes, this, true) }
    .data

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
        productQty = 0,
        productIncrement = mutableListOf()
    )

}
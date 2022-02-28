package com.rmyfactory.rmyinventorybarcode.util

import java.text.SimpleDateFormat
import java.util.*

object Functions {

    fun millisToOrderId(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd:MM:yy:HH:mm:ss", Locale.ENGLISH)
        val dateString = sdf.format(timeInMillis)

        val dateSplit = dateString.split(":")

        return "${dateSplit[2]}${dateSplit[1]}${dateSplit[0]}${dateSplit[3]}${dateSplit[4]}${dateSplit[5]}"
    }

}
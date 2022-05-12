package com.rmyfactory.inventorybarcode.util

sealed class ResultResponse<out T> {
    data class Success<out T>(val data: T): ResultResponse<T>()
    data class Failure(val exception: Throwable?): ResultResponse<Nothing>()
    data class Loading(val status: Boolean?): ResultResponse<Nothing>()
}
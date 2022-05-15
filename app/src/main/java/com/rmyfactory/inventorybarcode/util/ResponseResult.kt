package com.rmyfactory.inventorybarcode.util

sealed class ResponseResult<out T> {
    data class Success<out T>(val data: T): ResponseResult<T>()
    data class Failure(val exception: Throwable?): ResponseResult<Nothing>()
    data class Loading(val status: Boolean?): ResponseResult<Nothing>()
}
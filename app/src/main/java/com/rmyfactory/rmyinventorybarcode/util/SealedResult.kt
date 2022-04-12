package com.rmyfactory.rmyinventorybarcode.util

sealed class SealedResult<out T> {
    data class Success<out T>(val data: T): SealedResult<T>()
    data class Failure(val exception: Throwable?): SealedResult<Nothing>()
    data class Loading(val status: Boolean?): SealedResult<Nothing>()
}
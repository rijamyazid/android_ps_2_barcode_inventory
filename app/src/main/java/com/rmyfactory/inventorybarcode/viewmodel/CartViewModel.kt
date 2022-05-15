package com.rmyfactory.inventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.inventorybarcode.model.data.local.model.holder.CartHolder
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import com.rmyfactory.inventorybarcode.model.repository.MainRepository
import com.rmyfactory.inventorybarcode.util.ResponseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    //    val itemList = mutableListOf<MutableMap<String, String>>()
//    val itemList = mutableListOf<OrderHolder>()
    val itemList = mutableListOf<CartHolder>()

    private val _productWithUnitsResult = MutableLiveData<ResponseResult<ProductWithUnits>>()
    val productWithUnitsResult : LiveData<ResponseResult<ProductWithUnits>> get() = _productWithUnitsResult

    fun readProductWithUnitsById(productId: String) = viewModelScope.launch(Dispatchers.IO) {
        ResponseResult.Loading(true)
        val productWithUnits = repository.susReadProductWithUnitsById(productId)
        productWithUnits?.let {
            ResponseResult.Success(productWithUnits)
        } ?: ResponseResult.Failure(Exception())
    }

}
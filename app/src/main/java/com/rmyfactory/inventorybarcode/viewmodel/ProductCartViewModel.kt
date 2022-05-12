package com.rmyfactory.inventorybarcode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductCartViewModel @Inject constructor(): ViewModel() {

    val productCartState = MutableLiveData(0)

    fun setProductCartState(state: Int) {
        productCartState.value = state
    }

    lateinit var productWithUnits: ProductWithUnits

}
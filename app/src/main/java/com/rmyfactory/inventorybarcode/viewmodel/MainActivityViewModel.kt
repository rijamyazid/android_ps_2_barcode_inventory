package com.rmyfactory.inventorybarcode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rmyfactory.inventorybarcode.model.data.local.model.with.ProductWithUnits
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(): ViewModel() {

    var productCartState = 0

    val productWithUnits = MutableLiveData<ProductWithUnits?>()

}
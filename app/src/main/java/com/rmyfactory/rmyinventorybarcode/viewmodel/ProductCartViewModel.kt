package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductCartViewModel @Inject constructor(): ViewModel() {

    val productCartState = MutableLiveData(0)

    fun setProductCartState(state: Int) {
        productCartState.value = state
    }

    lateinit var itemWithUnits: ItemWithUnits

}
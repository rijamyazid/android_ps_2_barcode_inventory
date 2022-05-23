package com.rmyfactory.inventorybarcode.viewmodel

import androidx.lifecycle.*
import com.rmyfactory.inventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val orderDetailId = MutableLiveData<String>()
    val orderDetailLiveData = orderDetailId.switchMap {
        liveData {
            emit(repository.susReadOrderWithProductById(it))
        }
    }.distinctUntilChanged()

    fun setOrderDetailId(orderId: String) {
        orderDetailId.value = orderId
    }

}
package com.rmyfactory.inventorybarcode.viewmodel

import androidx.lifecycle.*
import com.rmyfactory.inventorybarcode.model.data.local.model.with.OrderWithProducts
import com.rmyfactory.inventorybarcode.model.repository.MainRepository
import com.rmyfactory.inventorybarcode.util.ResponseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val orderDetailId = MutableLiveData<String>()
    val orderDetailLiveData: LiveData<ResponseResult<OrderWithProducts>> = orderDetailId.switchMap {
        liveData {
            emit(ResponseResult.Loading(true))
            val orderWithProducts = repository.susReadOrderWithProductById(it)
            if(orderWithProducts != null) emit(ResponseResult.Success(orderWithProducts)) else
                emit(ResponseResult.Failure(Exception()))

        }
    }.distinctUntilChanged()

    fun setOrderDetailId(orderId: String) {
        orderDetailId.value = orderId
    }

}
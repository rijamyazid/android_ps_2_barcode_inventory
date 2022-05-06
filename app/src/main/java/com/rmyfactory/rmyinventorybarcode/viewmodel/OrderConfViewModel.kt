package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderProductModel
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderConfViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    fun insertOrders(order: OrderModel, orders: List<OrderProductModel>) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertOrder(order)
            repository.insertOrderProducts(orders)
        }

}
package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.relations.OrderItemModel
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    fun insertOrders(order: OrderModel, orders: List<OrderItemModel>) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertOrder(order)
            repository.insertOrderItems(orders)
        }

}
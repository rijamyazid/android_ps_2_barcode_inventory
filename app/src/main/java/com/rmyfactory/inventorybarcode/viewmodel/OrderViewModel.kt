package com.rmyfactory.inventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.inventorybarcode.model.data.local.model.OrderModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.OrderProductModel
import com.rmyfactory.inventorybarcode.model.data.local.model.relations.ProductUnitModel
import com.rmyfactory.inventorybarcode.model.repository.MainRepository
import com.rmyfactory.inventorybarcode.util.ResponseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val _productWithUnitsResult = MutableLiveData<ResponseResult<Boolean>>()
    val productWithUnitsResult: LiveData<ResponseResult<Boolean>> get() = _productWithUnitsResult

    fun insertOrders(order: OrderModel, orders: List<OrderProductModel>) =
        viewModelScope.launch(Dispatchers.IO) {
            _productWithUnitsResult.postValue(ResponseResult.Loading(true))

            val productUnitModelList = mutableListOf<ProductUnitModel>()
            orders.forEach { orderData ->
                val productWithUnits = repository.susReadProductWithUnitsById(orderData.productId)
                if (productWithUnits != null) {
                    productWithUnits.productUnitList.forEach { productUnitData ->
                        if (productUnitData.unit.unitId.equals(orderData.unit, ignoreCase = true)) {
                            productUnitData.productUnit.stock =
                                if (productUnitData.productUnit.stock - orderData.qty < 0) {
                                    0
                                } else {
                                    productUnitData.productUnit.stock - orderData.qty
                                }
                            productUnitModelList.add(productUnitData.productUnit)
                        }
                    }

                    repository.updateProductUnits(productUnitModelList)
                    repository.insertOrder(order)
                    repository.insertOrderProducts(orders)

                    _productWithUnitsResult.postValue(ResponseResult.Success(true))
                } else {
                    _productWithUnitsResult.postValue(ResponseResult.Failure(Exception()))
                }
            }
        }
}
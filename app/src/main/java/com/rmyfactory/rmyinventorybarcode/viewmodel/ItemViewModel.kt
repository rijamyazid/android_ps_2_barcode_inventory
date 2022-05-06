package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.with.ItemWithUnits
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ItemViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val productQuery = MutableLiveData<String>()
    val productWithUnitsByQuery = productQuery.switchMap {
        if (it.isEmpty()) {
            repository.readItemWithUnits()
        } else {
            repository.readItemWithUnitsByQuery("$it%")
        }
    }

    fun updateProductQuery(query: String) {
        productQuery.value = query
    }

    fun readItemWithUnits(): LiveData<List<ItemWithUnits>> {
        return repository.readItemWithUnits()
    }

}
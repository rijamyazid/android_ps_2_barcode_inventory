package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.ItemModel
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    fun insertItem(item: ItemModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertItem(item)
    }

    fun deleteItemById(itemId: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItemById(itemId)
    }

    fun readItemById(itemId: String): LiveData<ItemModel> {
        return repository.readItemById(itemId)
    }

    fun updateItem(item: ItemModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateItem(item)
    }

}
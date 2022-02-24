package com.rmyfactory.rmyinventorybarcode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmyfactory.rmyinventorybarcode.model.data.local.ItemModel
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

}
package com.rmyfactory.inventorybarcode.viewmodel

import androidx.lifecycle.ViewModel
import com.rmyfactory.inventorybarcode.model.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogViewModel
@Inject constructor(private val repository: MainRepository) : ViewModel() {

    fun readOrderWithItems() = repository.readOrderWithProducts()

}